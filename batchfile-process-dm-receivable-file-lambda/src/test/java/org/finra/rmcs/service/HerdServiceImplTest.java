package org.finra.rmcs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.util.Arrays;
import org.finra.herd.sdk.api.BusinessObjectDataApi;
import org.finra.herd.sdk.invoker.ApiException;
import org.finra.herd.sdk.model.BusinessObjectData;
import org.finra.herd.sdk.model.BusinessObjectDataKey;
import org.finra.rmcs.common.service.oauth2.OAuth2ServiceImpl;
import org.finra.rmcs.exception.RetryableException;
import org.finra.rmcs.exception.UnRetryableException;
import org.finra.rmcs.service.impl.HerdServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class HerdServiceImplTest {

  @InjectMocks
  @Spy
  private HerdServiceImpl herdService;

  @Mock
  private BusinessObjectDataApi businessObjectDataApi;
  @Mock
  private OAuth2ServiceImpl oAuth2Service;


  @Test
  public void testGetBusinessObjectData_success() throws ApiException {
    String expected = "testNameSpace";
    BusinessObjectData businessObjectData = new BusinessObjectData();
    businessObjectData.setNamespace(expected);
    BusinessObjectDataKey businessObjectDataKey = new BusinessObjectDataKey();
    businessObjectDataKey.setSubPartitionValues(Arrays.asList(expected));
    Mockito.doReturn(businessObjectDataApi).when(herdService).getBusinessObjectDataApi();
    Mockito.when(
            businessObjectDataApi.businessObjectDataGetBusinessObjectData(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any()))
        .thenReturn(businessObjectData);
    businessObjectData = herdService.getBusinessObjectData(businessObjectDataKey);
    Mockito.verify(businessObjectDataApi)
        .businessObjectDataGetBusinessObjectData(
            any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
            any());
    assertEquals(expected, businessObjectData.getNamespace());
  }

  // HttpStatus code 502 and Retryable
  @Test
  public void testGetBusinessObjectData_retryableException() throws ApiException {
    BusinessObjectDataKey businessObjectDataKey = new BusinessObjectDataKey();
    businessObjectDataKey.setSubPartitionValues(Arrays.asList("test"));
    String expect = "testMessage";
    Mockito.doReturn(businessObjectDataApi).when(herdService).getBusinessObjectDataApi();
    Mockito.when(
            businessObjectDataApi.businessObjectDataGetBusinessObjectData(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any()))
        .thenThrow(new ApiException(502, expect));
    Throwable exception =
        assertThrows(
            RetryableException.class,
            () -> herdService.getBusinessObjectData(businessObjectDataKey));
    assertEquals(expect, exception.getMessage());
  }

  // HttpStatus code 400 and unRetryable
  @Test
  public void testGetBusinessObjectData_unRetryableException() throws ApiException {
    BusinessObjectDataKey businessObjectDataKey = new BusinessObjectDataKey();
    businessObjectDataKey.setSubPartitionValues(Arrays.asList("test"));
    String expect = "testMessage";
    Mockito.doReturn(businessObjectDataApi).when(herdService).getBusinessObjectDataApi();
    Mockito.when(
            businessObjectDataApi.businessObjectDataGetBusinessObjectData(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any()))
        .thenThrow(new ApiException(400, expect));
    Throwable exception =
        assertThrows(
            UnRetryableException.class,
            () -> herdService.getBusinessObjectData(businessObjectDataKey));
    assertEquals(expect, exception.getMessage());
  }

  // HttpStatus code 401 and unRetryable
  @Test
  public void testGetBusinessObjectData_UnRetryableException() throws ApiException {
    BusinessObjectDataKey businessObjectDataKey = new BusinessObjectDataKey();
    businessObjectDataKey.setSubPartitionValues(Arrays.asList("test"));
    String expect = "testMessage";
    Mockito.doReturn(businessObjectDataApi).when(herdService).getBusinessObjectDataApi();
    Mockito.when(
            businessObjectDataApi.businessObjectDataGetBusinessObjectData(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any()))
        .thenThrow(new ApiException(401, expect));
    assertThrows(UnRetryableException.class, () -> {
      herdService.getBusinessObjectData(businessObjectDataKey);
    });
  }

  // HttpStatus code 500 and unRetryable
  @Test
  public void testGetBusinessObjectData_UnRetryableException2() throws ApiException {
    BusinessObjectDataKey businessObjectDataKey = new BusinessObjectDataKey();
    businessObjectDataKey.setSubPartitionValues(Arrays.asList("test"));
    String expect = "testMessage";
    Mockito.doReturn(businessObjectDataApi).when(herdService).getBusinessObjectDataApi();
    Mockito.when(
            businessObjectDataApi.businessObjectDataGetBusinessObjectData(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any()))
        .thenThrow(new ApiException(500, expect));
    assertThrows(UnRetryableException.class, () -> {
      herdService.getBusinessObjectData(businessObjectDataKey);
    });
  }

  // HttpStatus code 399 and Retryable
  @Test
  public void testGetBusinessObjectData_RetryableException() throws ApiException {
    BusinessObjectDataKey businessObjectDataKey = new BusinessObjectDataKey();
    businessObjectDataKey.setSubPartitionValues(Arrays.asList("test"));
    String expect = "testMessage";
    Mockito.doReturn(businessObjectDataApi).when(herdService).getBusinessObjectDataApi();
    Mockito.when(
            businessObjectDataApi.businessObjectDataGetBusinessObjectData(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any()))
        .thenThrow(new ApiException(399, expect));
    assertThrows(RetryableException.class, () -> {
      herdService.getBusinessObjectData(businessObjectDataKey);
    });
  }
}
