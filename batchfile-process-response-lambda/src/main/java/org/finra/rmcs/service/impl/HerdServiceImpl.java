package org.finra.rmcs.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.finra.herd.sdk.api.BusinessObjectDataApi;
import org.finra.herd.sdk.api.BusinessObjectDataStatusApi;
import org.finra.herd.sdk.api.BusinessObjectDataStorageFileApi;
import org.finra.herd.sdk.api.BusinessObjectFormatApi;
import org.finra.herd.sdk.invoker.ApiClient;
import org.finra.herd.sdk.invoker.ApiException;
import org.finra.herd.sdk.model.Attribute;
import org.finra.herd.sdk.model.BusinessObjectData;
import org.finra.herd.sdk.model.BusinessObjectDataCreateRequest;
import org.finra.herd.sdk.model.BusinessObjectDataKey;
import org.finra.herd.sdk.model.BusinessObjectDataStatusUpdateRequest;
import org.finra.herd.sdk.model.BusinessObjectDataStatusUpdateResponse;
import org.finra.herd.sdk.model.BusinessObjectDataStorageFilesCreateRequest;
import org.finra.herd.sdk.model.BusinessObjectDataStorageFilesCreateResponse;
import org.finra.herd.sdk.model.BusinessObjectFormat;
import org.finra.herd.sdk.model.StorageUnitCreateRequest;
import org.finra.rmcs.common.service.oauth2.OAuth2ServiceImpl;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.constants.HerdEntity;
import org.finra.rmcs.exception.RetryableException;
import org.finra.rmcs.exception.UnRetryableException;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;
import org.finra.rmcs.service.HerdService;
import org.finra.rmcs.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;

@Slf4j
@Service
public class HerdServiceImpl implements HerdService {

  @Value("${dm.endpoint}")
  private final String dmBaseEndpoint;

  private final OAuth2ServiceImpl oAuth2Service;

  @Autowired
  public HerdServiceImpl(
      @Value("${dm.endpoint}") String dmBaseEndpoint,
      OAuth2ServiceImpl oAuth2Service) {
    this.dmBaseEndpoint = dmBaseEndpoint;
    this.oAuth2Service = oAuth2Service;
  }

  @Override
  public BusinessObjectFormat getBusinessObjectFormat(
      BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent,
      HerdEntity herdEntity) {
    String businessObjectDefinitionName =
        String.format(
            herdEntity.getBusinessObjectDefinitionName(),
            Util.getRevenueStreamFromNameSpace(businessObjectDataStatusChangeEvent));
    log.info("Retrieving BusinessObjectFormat for {}", businessObjectDefinitionName);

    try {
      return getBusinessObjectFormatApi().businessObjectFormatGetBusinessObjectFormat(
          businessObjectDataStatusChangeEvent.getBusinessObjectDataKey().getNamespace(),
          businessObjectDefinitionName,
          herdEntity.getBusinessObjectFormatUsage(),
          herdEntity.getBusinessObjectFormatFileType(),
          null);
    } catch (ApiException apiException) {
      log.error(
          "Exception during retrieval of BusinessObjectFormat for {}, Cause: {}, ResponseBody: {}, Code: {}",
          businessObjectDataStatusChangeEvent
              .getBusinessObjectDataKey()
              .getBusinessObjectDefinitionName(),
          apiException.getMessage(),
          apiException.getResponseBody(),
          apiException.getCode());
      // quit retry due to 400 series (BadRequest/Unauthorized) and 500 errors
      if (apiException.getCode() >= HttpStatus.SC_BAD_REQUEST
          && apiException.getCode() <= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
        throw new UnRetryableException(apiException.getMessage());
      }
      throw new RetryableException(apiException.getMessage());
    }
  }

  @Override
  public BusinessObjectData preRegisterBusinessObjectData(
      BusinessObjectFormat bizObjectFormat,
      BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent) {
    BusinessObjectDataCreateRequest request = new BusinessObjectDataCreateRequest();
    request.setNamespace(bizObjectFormat.getNamespace());
    request.setBusinessObjectDefinitionName(bizObjectFormat.getBusinessObjectDefinitionName());
    request.setBusinessObjectFormatUsage(bizObjectFormat.getBusinessObjectFormatUsage());
    request.setBusinessObjectFormatFileType(bizObjectFormat.getBusinessObjectFormatFileType());
    request.setBusinessObjectFormatVersion(bizObjectFormat.getBusinessObjectFormatVersion());
    request.setPartitionKey(bizObjectFormat.getPartitionKey());
    request.setPartitionValue(
        businessObjectDataStatusChangeEvent.getBusinessObjectDataKey().getPartitionValue());
    request.setSubPartitionValues(
        businessObjectDataStatusChangeEvent.getBusinessObjectDataKey().getSubPartitionValues());
    Attribute attribute = new Attribute();
    attribute.setName("businessObjectDefinitionName");
    attribute.setValue(bizObjectFormat.getBusinessObjectDefinitionName());
    request.setAttributes(Arrays.asList(attribute));
    request.setStatus(Constants.DM_STATUS_UPLOADING);

    StorageUnitCreateRequest storageUnit = new StorageUnitCreateRequest();
    storageUnit.setStorageName(Constants.RMCS_HERD_STORAGE);
    request.setStorageUnits(new ArrayList<>());
    // Set DiscoverStorageFiles attribute to FALSE
    storageUnit.setDiscoverStorageFiles(false);
    request.getStorageUnits().add(storageUnit);
    request.setCreateNewVersion(true);

    log.info("About to invoke BusinessObjectDataCreateRequest: {}", request);

    BusinessObjectData bizObjData = null;
    try {
      bizObjData = getBusinessObjectDataApi().businessObjectDataCreateBusinessObjectData(request);
    } catch (ApiException apiException) {
      log.error(
          "Exception during creation of BusinessObjectData for {}, Cause: {}, ResponseBody: {}, Code: {}",
          bizObjectFormat.getBusinessObjectDefinitionName(),
          apiException.getMessage(),
          apiException.getResponseBody(),
          apiException.getCode());
      // quit retry due to 400 series (BadRequest/Unauthorized) and 500 errors
      if (apiException.getCode() >= HttpStatus.SC_BAD_REQUEST
          && apiException.getCode() <= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
        throw new UnRetryableException(apiException.getMessage());
      }
      throw new RetryableException(apiException.getMessage());
    }

    if (bizObjData == null) {
      throw new RuntimeException(
          String.format(
              "Pre-registration of business object data for %s failed!",
              bizObjectFormat.getBusinessObjectDefinitionName()));
    }
    log.info("Successfully retrieved BusinessObjectData: {}", bizObjData);

    return bizObjData;
  }

  @Override
  public String uploadResponseFileToS3(
      BusinessObjectData bizObjData, String file, String fileName) {
    String directoryPath =
        bizObjData.getStorageUnits().get(0).getStorageDirectory().getDirectoryPath();
    Attribute destinationBucketName =
        bizObjData.getStorageUnits().get(0).getStorage().getAttributes().stream()
            .filter(attribute -> attribute.getName().equals(Constants.ATTR_BUCKET_NAME))
            .findAny()
            .orElse(null);
    Attribute kmsKeyId =
        bizObjData.getStorageUnits().get(0).getStorage().getAttributes().stream()
            .filter(attribute -> attribute.getName().equals(Constants.ATTR_KMS_KEY_ID))
            .findAny()
            .orElse(null);

    // Validate the extracted values
    if (StringUtils.isBlank(directoryPath) || destinationBucketName == null || kmsKeyId == null) {
      throw new RuntimeException(
          String.format(
              "Invalid directoryPath %s, BucketName %s or KmsKey %s extracted from BusinessObjectData %s",
              directoryPath,
              destinationBucketName,
              kmsKeyId,
              bizObjData.getBusinessObjectDefinitionName()));
    }

    String bucket = destinationBucketName.getValue();
    String kmsKey = kmsKeyId.getValue();
    String s3Key = String.format("%s%s%s", directoryPath, "/", fileName);
    log.info("DestinationBucket={}, s3Key={}, kmsKey={}", bucket, s3Key, kmsKey);

    UploadRequest uploadRequest =
        UploadRequest.builder()
            .putObjectRequest(
                request ->
                    request
                        .bucket(bucket)
                        .key(s3Key)
                        .ssekmsKeyId(kmsKey)
                        .serverSideEncryption(ServerSideEncryption.AWS_KMS))
            .requestBody(AsyncRequestBody.fromString(file))
            .build();

    try (S3TransferManager s3TransferManager = getS3transferManager()) {
      log.info("start uploading file: {}", fileName);
      s3TransferManager.upload(uploadRequest).completionFuture().join();
      log.info("file upload completed: {}", fileName);
    }
    return s3Key;
  }

  @Override
  public BusinessObjectData retrieveBusinessObjectData(
      BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent) {
    BusinessObjectDataKey businessObjectDataKey =
        businessObjectDataStatusChangeEvent.getBusinessObjectDataKey();
    try {
      return getBusinessObjectDataApi().businessObjectDataGetBusinessObjectData(
          businessObjectDataKey.getNamespace(),
          businessObjectDataKey.getBusinessObjectDefinitionName(),
          businessObjectDataKey.getBusinessObjectFormatUsage(),
          businessObjectDataKey.getBusinessObjectFormatFileType(),
          null,
          businessObjectDataKey.getPartitionValue(),
          businessObjectDataKey.getSubPartitionValues().get(0),
          businessObjectDataKey.getBusinessObjectFormatVersion(),
          businessObjectDataKey.getBusinessObjectDataVersion(),
          null,
          false,
          false,
          false);
    } catch (ApiException apiException) {
      log.error(
          "Exception during retrieval of BusinessObjectData for {}, Cause: {}, ResponseBody: {}, Code: {}",
          businessObjectDataKey.getBusinessObjectDefinitionName(),
          apiException.getMessage(),
          apiException.getResponseBody(),
          apiException.getCode());
      // quit retry due to 400 series (BadRequest/Unauthorized) and 500 errors
      if (apiException.getCode() >= HttpStatus.SC_BAD_REQUEST
          && apiException.getCode() <= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
        throw new UnRetryableException(apiException.getMessage());
      }
      throw new RetryableException(apiException.getMessage());
    }
  }

  @Override
  public void updateBusinessObjectDataStorage(BusinessObjectData bizObjData) {
    BusinessObjectDataStorageFilesCreateRequest request =
        new BusinessObjectDataStorageFilesCreateRequest();
    request.setNamespace(bizObjData.getNamespace());
    request.setBusinessObjectDefinitionName(bizObjData.getBusinessObjectDefinitionName());
    request.setBusinessObjectFormatUsage(bizObjData.getBusinessObjectFormatUsage());
    request.setBusinessObjectFormatFileType(bizObjData.getBusinessObjectFormatFileType());
    request.setBusinessObjectFormatVersion(bizObjData.getBusinessObjectFormatVersion());
    request.setBusinessObjectDataVersion(bizObjData.getVersion());
    request.setStorageName(Constants.RMCS_HERD_STORAGE);
    request.setPartitionValue(bizObjData.getPartitionValue());
    request.setSubPartitionValues(bizObjData.getSubPartitionValues());
    request.setDiscoverStorageFiles(true); // makes the file visible

    log.info("Start updating herd store with the request: {}", request);
    BusinessObjectDataStorageFilesCreateResponse bodStorageFileCreateResponse;
    try {
      bodStorageFileCreateResponse =
          getBusinessObjectDataStorageFileApi().businessObjectDataStorageFileCreateBusinessObjectDataStorageFiles(
              request);
    } catch (ApiException apiException) {
      log.error(
          "Exception during updating Herd storage for {}, Cause: {}, ResponseBody: {}, Code: {}",
          bizObjData.getBusinessObjectDefinitionName(),
          apiException.getMessage(),
          apiException.getResponseBody(),
          apiException.getCode());
      // quit retry due to 400 series (BadRequest/Unauthorized) and 500 errors

      if (apiException.getCode() >= HttpStatus.SC_BAD_REQUEST
          && apiException.getCode() <= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
        throw new UnRetryableException(apiException.getMessage());
      }
      throw new RetryableException(apiException.getMessage());
    }

    if (bodStorageFileCreateResponse == null) {
      throw new RuntimeException("Herd storage update failed!");
    }
    log.info(
        "Successfully updated BusinessObjectDataStorage with BusinessObjectDataStorageFilesCreateResponse: {}",
        bodStorageFileCreateResponse);
  }

  @Override
  public String updateBusinessObjectDataStatus(
      BusinessObjectData businessObjectData, String status) {
    BusinessObjectDataStatusUpdateRequest bodStatusUpdateRequest =
        new BusinessObjectDataStatusUpdateRequest();
    bodStatusUpdateRequest.setStatus(status);

    BusinessObjectDataStatusUpdateResponse bizObjDataStatusUpdateResponse;

    try {
      bizObjDataStatusUpdateResponse =
          getBusinessObjectDataStatusApi().businessObjectDataStatusUpdateBusinessObjectDataStatus1(
              businessObjectData.getNamespace(),
              businessObjectData.getBusinessObjectDefinitionName(),
              businessObjectData.getBusinessObjectFormatUsage(),
              businessObjectData.getBusinessObjectFormatFileType(),
              businessObjectData.getBusinessObjectFormatVersion(),
              businessObjectData.getPartitionValue(),
              businessObjectData.getSubPartitionValues().get(0),
              businessObjectData.getVersion(),
              bodStatusUpdateRequest);
    } catch (ApiException apiException) {
      log.error(
          "Exception during updating updateBusinessObjectDataStatus for {}, Cause: {}, ResponseBody: {}, Code: {}",
          businessObjectData.getBusinessObjectDefinitionName(),
          apiException.getMessage(),
          apiException.getResponseBody(),
          apiException.getCode());
      // quit retry due to 400 series (BadRequest/Unauthorized) and 500 errors

      if (apiException.getCode() >= HttpStatus.SC_BAD_REQUEST
          && apiException.getCode() <= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
        throw new UnRetryableException(apiException.getMessage());
      }
      throw new RetryableException(apiException.getMessage());
    }

    if (bizObjDataStatusUpdateResponse == null) {
      throw new RuntimeException(
          "Herd Registration Status update for business object data failed!");
    }
    log.info(
        "Successfully retrieved BusinessObjectDataStatusUpdateResponse: {}",
        bizObjDataStatusUpdateResponse);

    return bizObjDataStatusUpdateResponse.getStatus();
  }

  @Bean
  @SneakyThrows
  public ApiClient apiClient() {
    log.info("Herd endpoint: {}", dmBaseEndpoint);
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(dmBaseEndpoint);
    apiClient.setAccessToken(oAuth2Service.getAccessToken());
    return apiClient;
  }

  public BusinessObjectFormatApi getBusinessObjectFormatApi() {
    return new BusinessObjectFormatApi(apiClient());
  }

  public BusinessObjectDataApi getBusinessObjectDataApi() {
    return new BusinessObjectDataApi(apiClient());
  }

  public S3TransferManager getS3transferManager() {
    return S3TransferManager.builder().build();
  }

  public BusinessObjectDataStorageFileApi getBusinessObjectDataStorageFileApi() {
    return new BusinessObjectDataStorageFileApi(apiClient());
  }

  public BusinessObjectDataStatusApi getBusinessObjectDataStatusApi() {
    return new BusinessObjectDataStatusApi(apiClient());
  }
}
