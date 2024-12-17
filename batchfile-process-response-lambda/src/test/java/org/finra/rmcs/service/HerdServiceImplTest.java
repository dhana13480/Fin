package org.finra.rmcs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.StringUtils;
import org.finra.herd.sdk.api.BusinessObjectDataApi;
import org.finra.herd.sdk.api.BusinessObjectDataStatusApi;
import org.finra.herd.sdk.api.BusinessObjectDataStorageFileApi;
import org.finra.herd.sdk.api.BusinessObjectFormatApi;
import org.finra.herd.sdk.invoker.ApiException;
import org.finra.herd.sdk.model.BusinessObjectData;
import org.finra.herd.sdk.model.BusinessObjectDataCreateRequest;
import org.finra.herd.sdk.model.BusinessObjectDataKey;
import org.finra.herd.sdk.model.BusinessObjectDataStatusUpdateResponse;
import org.finra.herd.sdk.model.BusinessObjectDataStorageFilesCreateRequest;
import org.finra.herd.sdk.model.BusinessObjectDataStorageFilesCreateResponse;
import org.finra.herd.sdk.model.BusinessObjectFormat;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.constants.HerdEntity;
import org.finra.rmcs.exception.RetryableException;
import org.finra.rmcs.exception.UnRetryableException;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;
import org.finra.rmcs.service.impl.HerdServiceImpl;
import org.finra.rmcs.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.internal.model.DefaultUpload;
import software.amazon.awssdk.transfer.s3.internal.progress.DefaultTransferProgress;
import software.amazon.awssdk.transfer.s3.model.CompletedUpload;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;

@SpringJUnitConfig
public class HerdServiceImplTest {

  @InjectMocks
  @Spy
  private HerdServiceImpl herdService;

  @Mock
  private BusinessObjectFormatApi businessObjectFormatApi;

  @Mock
  private BusinessObjectDataApi businessObjectDataApi;

  @Mock
  private S3TransferManager s3TransferManager;

  @Mock
  private DefaultTransferProgress defaultTransferProgress;

  @Mock
  private BusinessObjectDataStorageFileApi businessObjectDataStorageFileApi;

  @Mock
  private BusinessObjectDataStatusApi businessObjectDataStatusApi;

  @Test
  public void testGetBusinessObjectFormat() throws ApiException {
    String expected = "RMCS-APIBI-RECEIVABLES-OUT-FATAL";
    BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent =
        new BusinessObjectDataStatusChangeEvent();
    BusinessObjectDataKey businessObjectDataKey = new BusinessObjectDataKey();
    businessObjectDataKey.setNamespace("RMCS-APIBI");
    businessObjectDataStatusChangeEvent.setBusinessObjectDataKey(businessObjectDataKey);
    BusinessObjectFormat businessObjectFormat = new BusinessObjectFormat();
    businessObjectFormat.setBusinessObjectDefinitionName(expected);
    Mockito.doReturn(businessObjectFormatApi).when(herdService).getBusinessObjectFormatApi();
    Mockito.when(
            businessObjectFormatApi.businessObjectFormatGetBusinessObjectFormat(
                any(), any(), any(), any(), any()))
        .thenReturn(businessObjectFormat);
    BusinessObjectFormat actual =
        herdService.getBusinessObjectFormat(
            businessObjectDataStatusChangeEvent, HerdEntity.findByFileName("fatal"));
    Mockito.verify(businessObjectFormatApi)
        .businessObjectFormatGetBusinessObjectFormat(any(), any(), any(), any(), any());
    assertEquals(expected, actual.getBusinessObjectDefinitionName());
  }

  @Test
  public void testGetBusinessObjectFormat_retryableException() throws ApiException {
    String expected = "testMessage";
    BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent =
        new BusinessObjectDataStatusChangeEvent();
    BusinessObjectDataKey businessObjectDataKey = new BusinessObjectDataKey();
    businessObjectDataKey.setNamespace("RMCS-APIBI");
    businessObjectDataStatusChangeEvent.setBusinessObjectDataKey(businessObjectDataKey);
    Mockito.doReturn(businessObjectFormatApi).when(herdService).getBusinessObjectFormatApi();
    Mockito.when(
            businessObjectFormatApi.businessObjectFormatGetBusinessObjectFormat(
                any(), any(), any(), any(), any()))
        .thenThrow(new ApiException(502, expected));
    Throwable exception =
        assertThrows(
            RetryableException.class,
            () ->
                herdService.getBusinessObjectFormat(
                    businessObjectDataStatusChangeEvent, HerdEntity.FATAL));
    assertEquals(expected, exception.getMessage());
  }

  @Test
  public void testGetBusinessObjectFormat_unRetryableException() throws ApiException {
    String expected = "testMessage";
    BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent =
        new BusinessObjectDataStatusChangeEvent();
    BusinessObjectDataKey businessObjectDataKey = new BusinessObjectDataKey();
    businessObjectDataKey.setNamespace("RMCS-APIBI");
    businessObjectDataStatusChangeEvent.setBusinessObjectDataKey(businessObjectDataKey);
    Mockito.doReturn(businessObjectFormatApi).when(herdService).getBusinessObjectFormatApi();
    Mockito.when(
            businessObjectFormatApi.businessObjectFormatGetBusinessObjectFormat(
                any(), any(), any(), any(), any()))
        .thenThrow(new ApiException(400, expected));
    Throwable exception =
        assertThrows(
            UnRetryableException.class,
            () ->
                herdService.getBusinessObjectFormat(
                    businessObjectDataStatusChangeEvent, HerdEntity.FATAL));
    assertEquals(expected, exception.getMessage());
  }

  @Test
  public void testPreRegisterBusinessObjectData() throws ApiException {
    String expected = "RMCS-APIBI-RECEIVABLES-OUT-FATAL";
    BusinessObjectData businessObjectData = new BusinessObjectData();
    businessObjectData.setBusinessObjectDefinitionName(expected);
    Mockito.doReturn(businessObjectDataApi).when(herdService).getBusinessObjectDataApi();
    Mockito.when(
            businessObjectDataApi.businessObjectDataCreateBusinessObjectData(
                any(BusinessObjectDataCreateRequest.class)))
        .thenReturn(businessObjectData);
    BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent =
        new BusinessObjectDataStatusChangeEvent();
    businessObjectDataStatusChangeEvent.setBusinessObjectDataKey(new BusinessObjectDataKey());
    BusinessObjectData actual =
        herdService.preRegisterBusinessObjectData(
            new BusinessObjectFormat(), businessObjectDataStatusChangeEvent);
    Mockito.verify(businessObjectDataApi)
        .businessObjectDataCreateBusinessObjectData(any(BusinessObjectDataCreateRequest.class));
    assertEquals(expected, actual.getBusinessObjectDefinitionName());
  }

  @Test
  public void testPreRegisterBusinessObjectData_whenObjIsNull() throws ApiException {
    String expected = "RMCS-APIBI-RECEIVABLES-OUT-FATAL";
    BusinessObjectData businessObjectData = new BusinessObjectData();
    businessObjectData.setBusinessObjectDefinitionName(expected);
    Mockito.doReturn(businessObjectDataApi).when(herdService).getBusinessObjectDataApi();
    Mockito.when(
            businessObjectDataApi.businessObjectDataCreateBusinessObjectData(
                any(BusinessObjectDataCreateRequest.class)))
        .thenReturn(null);
    BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent =
        new BusinessObjectDataStatusChangeEvent();
    businessObjectDataStatusChangeEvent.setBusinessObjectDataKey(new BusinessObjectDataKey());
    Throwable exception =
        assertThrows(
            RuntimeException.class,
            () ->
                herdService.preRegisterBusinessObjectData(
                    new BusinessObjectFormat(), businessObjectDataStatusChangeEvent));
    Mockito.verify(businessObjectDataApi)
        .businessObjectDataCreateBusinessObjectData(any(BusinessObjectDataCreateRequest.class));
    assertTrue(
        exception
            .getMessage()
            .contains("Pre-registration of business object data for null failed!"));
  }

  @Test
  public void testPreRegisterBusinessObjectData_retryableException() throws ApiException {
    String expected = "testMessage";
    Mockito.doReturn(businessObjectDataApi).when(herdService).getBusinessObjectDataApi();
    Mockito.when(
            businessObjectDataApi.businessObjectDataCreateBusinessObjectData(
                any(BusinessObjectDataCreateRequest.class)))
        .thenThrow(new ApiException(502, expected));
    BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent =
        new BusinessObjectDataStatusChangeEvent();
    businessObjectDataStatusChangeEvent.setBusinessObjectDataKey(new BusinessObjectDataKey());
    Throwable exception =
        assertThrows(
            RetryableException.class,
            () ->
                herdService.preRegisterBusinessObjectData(
                    new BusinessObjectFormat(), businessObjectDataStatusChangeEvent));
    assertEquals(expected, exception.getMessage());
  }

  @Test
  public void testPreRegisterBusinessObjectData_unRetryableException() throws ApiException {
    String expected = "testMessage";
    Mockito.doReturn(businessObjectDataApi).when(herdService).getBusinessObjectDataApi();
    Mockito.when(
            businessObjectDataApi.businessObjectDataCreateBusinessObjectData(
                any(BusinessObjectDataCreateRequest.class)))
        .thenThrow(new ApiException(400, expected));
    BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent =
        new BusinessObjectDataStatusChangeEvent();
    businessObjectDataStatusChangeEvent.setBusinessObjectDataKey(new BusinessObjectDataKey());
    Throwable exception =
        assertThrows(
            UnRetryableException.class,
            () ->
                herdService.preRegisterBusinessObjectData(
                    new BusinessObjectFormat(), businessObjectDataStatusChangeEvent));
    assertEquals(expected, exception.getMessage());
  }

  @Test
  public void testUploadResponseFileToS3() {
    String expected = "testPath/testfile_fatal.jsonl";
    BusinessObjectData businessObjectData = TestUtil.getBusinessObjectData(true);
    Mockito.doReturn(s3TransferManager).when(herdService).getS3transferManager();
    Mockito.when(s3TransferManager.upload(any(UploadRequest.class)))
        .thenReturn(
            new DefaultUpload(
                CompletableFuture.completedFuture(
                    CompletedUpload.builder()
                        .response(PutObjectResponse.builder().build())
                        .build()),
                defaultTransferProgress));
    String s3Key =
        herdService.uploadResponseFileToS3(
            businessObjectData, StringUtils.EMPTY, "testfile_fatal.jsonl");
    assertEquals(expected, s3Key);
  }

  @Test
  public void testUploadResponseFileToS3_whenException() {
    String expected =
        "Invalid directoryPath , BucketName null or KmsKey null extracted from BusinessObjectData RMCS-APIBI-RECEIVABLES-IN";
    BusinessObjectData businessObjectData = TestUtil.getBusinessObjectData(false);
    Throwable exception =
        assertThrows(
            RuntimeException.class,
            () ->
                herdService.uploadResponseFileToS3(
                    businessObjectData, StringUtils.EMPTY, "testfile_fatal.jsonl"));
    assertEquals(expected, exception.getMessage());
  }

  @Test
  public void testRetrieveBusinessObjectData() throws ApiException {
    BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent =
        new BusinessObjectDataStatusChangeEvent();
    BusinessObjectDataKey businessObjectDataKey = new BusinessObjectDataKey();
    businessObjectDataKey.setSubPartitionValues(Arrays.asList("testSubPartitionValue"));
    businessObjectDataStatusChangeEvent.setBusinessObjectDataKey(businessObjectDataKey);
    String expected = "RMCS-APIBI-RECEIVABLES-OUT-FATAL";
    BusinessObjectData businessObjectData = new BusinessObjectData();
    businessObjectData.setBusinessObjectDefinitionName(expected);
    Mockito.doReturn(businessObjectDataApi).when(herdService).getBusinessObjectDataApi();
    Mockito.when(
            businessObjectDataApi.businessObjectDataGetBusinessObjectData(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any()))
        .thenReturn(businessObjectData);
    BusinessObjectData actual =
        herdService.retrieveBusinessObjectData(businessObjectDataStatusChangeEvent);
    assertEquals(expected, actual.getBusinessObjectDefinitionName());
  }

  @Test
  public void testRetrieveBusinessObjectData_retryableException() throws ApiException {
    BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent =
        new BusinessObjectDataStatusChangeEvent();
    BusinessObjectDataKey businessObjectDataKey = new BusinessObjectDataKey();
    businessObjectDataKey.setSubPartitionValues(Arrays.asList("testSubPartitionValue"));
    businessObjectDataStatusChangeEvent.setBusinessObjectDataKey(businessObjectDataKey);
    String expected = "testMessage";
    Mockito.doReturn(businessObjectDataApi).when(herdService).getBusinessObjectDataApi();
    Mockito.when(
            businessObjectDataApi.businessObjectDataGetBusinessObjectData(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any()))
        .thenThrow(new ApiException(502, expected));
    Throwable exception =
        assertThrows(
            RetryableException.class,
            () -> herdService.retrieveBusinessObjectData(businessObjectDataStatusChangeEvent));
    assertEquals(expected, exception.getMessage());
  }

  @Test
  public void testRetrieveBusinessObjectData_unRetryableException() throws ApiException {
    BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent =
        new BusinessObjectDataStatusChangeEvent();
    BusinessObjectDataKey businessObjectDataKey = new BusinessObjectDataKey();
    businessObjectDataKey.setSubPartitionValues(Arrays.asList("testSubPartitionValue"));
    businessObjectDataStatusChangeEvent.setBusinessObjectDataKey(businessObjectDataKey);
    String expected = "testMessage";
    Mockito.doReturn(businessObjectDataApi).when(herdService).getBusinessObjectDataApi();
    Mockito.when(
            businessObjectDataApi.businessObjectDataGetBusinessObjectData(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any()))
        .thenThrow(new ApiException(400, expected));
    Throwable exception =
        assertThrows(
            UnRetryableException.class,
            () -> herdService.retrieveBusinessObjectData(businessObjectDataStatusChangeEvent));
    assertEquals(expected, exception.getMessage());
  }

  @Test
  public void testUpdateBusinessObjectDataStorage() throws ApiException {
    BusinessObjectData businessObjectData = TestUtil.getBusinessObjectData(true);
    Mockito.doReturn(businessObjectDataStorageFileApi).when(herdService)
        .getBusinessObjectDataStorageFileApi();
    Mockito.when(
            businessObjectDataStorageFileApi
                .businessObjectDataStorageFileCreateBusinessObjectDataStorageFiles(
                    any(BusinessObjectDataStorageFilesCreateRequest.class)))
        .thenReturn(new BusinessObjectDataStorageFilesCreateResponse());
    herdService.updateBusinessObjectDataStorage(businessObjectData);
    Mockito.verify(businessObjectDataStorageFileApi)
        .businessObjectDataStorageFileCreateBusinessObjectDataStorageFiles(
            any(BusinessObjectDataStorageFilesCreateRequest.class));
  }

  @Test
  public void testUpdateBusinessObjectDataStorage_whenResponseNull() throws ApiException {
    BusinessObjectData businessObjectData = TestUtil.getBusinessObjectData(true);
    Mockito.doReturn(businessObjectDataStorageFileApi).when(herdService)
        .getBusinessObjectDataStorageFileApi();
    Mockito.when(
            businessObjectDataStorageFileApi
                .businessObjectDataStorageFileCreateBusinessObjectDataStorageFiles(
                    any(BusinessObjectDataStorageFilesCreateRequest.class)))
        .thenReturn(null);
    Throwable exception =
        assertThrows(
            RuntimeException.class,
            () -> herdService.updateBusinessObjectDataStorage(businessObjectData));
    Mockito.verify(businessObjectDataStorageFileApi)
        .businessObjectDataStorageFileCreateBusinessObjectDataStorageFiles(
            any(BusinessObjectDataStorageFilesCreateRequest.class));
    assertEquals("Herd storage update failed!", exception.getMessage());
  }

  @Test
  public void testUpdateBusinessObjectDataStorage_retryableException() throws ApiException {
    String expected = "testMessage";
    BusinessObjectData businessObjectData = TestUtil.getBusinessObjectData(true);
    Mockito.doReturn(businessObjectDataStorageFileApi).when(herdService)
        .getBusinessObjectDataStorageFileApi();
    Mockito.when(
            businessObjectDataStorageFileApi
                .businessObjectDataStorageFileCreateBusinessObjectDataStorageFiles(
                    any(BusinessObjectDataStorageFilesCreateRequest.class)))
        .thenThrow(new ApiException(502, expected));
    Throwable exception =
        assertThrows(
            RetryableException.class,
            () -> herdService.updateBusinessObjectDataStorage(businessObjectData));
    assertEquals(expected, exception.getMessage());
  }

  @Test
  public void testUpdateBusinessObjectDataStorage_unRetryableException() throws ApiException {
    String expected = "testMessage";
    BusinessObjectData businessObjectData = TestUtil.getBusinessObjectData(true);
    Mockito.doReturn(businessObjectDataStorageFileApi).when(herdService)
        .getBusinessObjectDataStorageFileApi();
    Mockito.when(
            businessObjectDataStorageFileApi
                .businessObjectDataStorageFileCreateBusinessObjectDataStorageFiles(
                    any(BusinessObjectDataStorageFilesCreateRequest.class)))
        .thenThrow(new ApiException(400, expected));
    Throwable exception =
        assertThrows(
            UnRetryableException.class,
            () -> herdService.updateBusinessObjectDataStorage(businessObjectData));
    assertEquals(expected, exception.getMessage());
  }

  @Test
  public void testUpdateBusinessObjectDataStatus() throws ApiException {
    String expected = Constants.DM_STATUS_VALID;
    BusinessObjectData businessObjectData = TestUtil.getBusinessObjectData(true);
    BusinessObjectDataStatusUpdateResponse businessObjectDataStatusUpdateResponse =
        new BusinessObjectDataStatusUpdateResponse();
    businessObjectDataStatusUpdateResponse.setStatus(expected);
    Mockito.doReturn(businessObjectDataStatusApi).when(herdService)
        .getBusinessObjectDataStatusApi();
    Mockito.when(
            businessObjectDataStatusApi.businessObjectDataStatusUpdateBusinessObjectDataStatus1(
                any(), any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(businessObjectDataStatusUpdateResponse);
    String actual = herdService.updateBusinessObjectDataStatus(businessObjectData, expected);
    Mockito.verify(businessObjectDataStatusApi)
        .businessObjectDataStatusUpdateBusinessObjectDataStatus1(
            any(), any(), any(), any(), any(), any(), any(), any(), any());
    assertEquals(expected, actual);
  }

  @Test
  public void testUpdateBusinessObjectDataStatus_whenResponseNull() throws ApiException {
    BusinessObjectData businessObjectData = TestUtil.getBusinessObjectData(true);
    Mockito.doReturn(businessObjectDataStatusApi).when(herdService)
        .getBusinessObjectDataStatusApi();
    Mockito.when(
            businessObjectDataStatusApi.businessObjectDataStatusUpdateBusinessObjectDataStatus1(
                any(), any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(null);
    Throwable exception =
        assertThrows(
            RuntimeException.class,
            () ->
                herdService.updateBusinessObjectDataStatus(
                    businessObjectData, Constants.DM_STATUS_VALID));
    Mockito.verify(businessObjectDataStatusApi)
        .businessObjectDataStatusUpdateBusinessObjectDataStatus1(
            any(), any(), any(), any(), any(), any(), any(), any(), any());
    assertEquals(
        "Herd Registration Status update for business object data failed!", exception.getMessage());
  }

  @Test
  public void testUpdateBusinessObjectDataStatus_retryableException() throws ApiException {
    String expected = "testMessage";
    BusinessObjectData businessObjectData = TestUtil.getBusinessObjectData(true);
    Mockito.doReturn(businessObjectDataStatusApi).when(herdService)
        .getBusinessObjectDataStatusApi();
    Mockito.when(
            businessObjectDataStatusApi.businessObjectDataStatusUpdateBusinessObjectDataStatus1(
                any(), any(), any(), any(), any(), any(), any(), any(), any()))
        .thenThrow(new ApiException(502, expected));
    Throwable exception =
        assertThrows(
            RetryableException.class,
            () ->
                herdService.updateBusinessObjectDataStatus(
                    businessObjectData, Constants.DM_STATUS_VALID));
    Mockito.verify(businessObjectDataStatusApi)
        .businessObjectDataStatusUpdateBusinessObjectDataStatus1(
            any(), any(), any(), any(), any(), any(), any(), any(), any());
    assertEquals(expected, exception.getMessage());
  }

  @Test
  public void testUpdateBusinessObjectDataStatus_unRetryableException() throws ApiException {
    String expected = "testMessage";
    BusinessObjectData businessObjectData = TestUtil.getBusinessObjectData(true);
    Mockito.doReturn(businessObjectDataStatusApi).when(herdService)
        .getBusinessObjectDataStatusApi();
    Mockito.when(
            businessObjectDataStatusApi.businessObjectDataStatusUpdateBusinessObjectDataStatus1(
                any(), any(), any(), any(), any(), any(), any(), any(), any()))
        .thenThrow(new ApiException(400, expected));
    Throwable exception =
        assertThrows(
            UnRetryableException.class,
            () ->
                herdService.updateBusinessObjectDataStatus(
                    businessObjectData, Constants.DM_STATUS_VALID));
    Mockito.verify(businessObjectDataStatusApi)
        .businessObjectDataStatusUpdateBusinessObjectDataStatus1(
            any(), any(), any(), any(), any(), any(), any(), any(), any());
    assertEquals(expected, exception.getMessage());
  }
}
