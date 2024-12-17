package org.finra.rmcs.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.finra.herd.sdk.api.BusinessObjectDataApi;
import org.finra.herd.sdk.invoker.ApiClient;
import org.finra.herd.sdk.invoker.ApiException;
import org.finra.herd.sdk.model.BusinessObjectData;
import org.finra.herd.sdk.model.BusinessObjectDataKey;
import org.finra.rmcs.common.service.oauth2.OAuth2ServiceImpl;
import org.finra.rmcs.exception.RetryableException;
import org.finra.rmcs.exception.UnRetryableException;
import org.finra.rmcs.service.HerdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
  public BusinessObjectData getBusinessObjectData(BusinessObjectDataKey businessObjectDataKey) {
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

  public BusinessObjectDataApi getBusinessObjectDataApi() {
    log.info("Herd endpoint: {}", dmBaseEndpoint);
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(dmBaseEndpoint);
    apiClient.setAccessToken(oAuth2Service.getAccessToken());
    return new BusinessObjectDataApi(apiClient);
  }
}
