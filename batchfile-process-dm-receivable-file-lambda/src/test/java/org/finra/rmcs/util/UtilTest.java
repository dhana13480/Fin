package org.finra.rmcs.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.StringUtils;
import org.finra.fidelius.FideliusClient;
import org.finra.herd.sdk.model.BusinessObjectData;
import org.finra.herd.sdk.model.BusinessObjectDataKey;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;
import org.finra.rmcs.util.TestUtil.Appender;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class UtilTest {

  @Test
  public void testGetPassword_whenSuccess() {
    Appender appender = TestUtil.setUpLogMonitorForClass(Util.class);
    String expected = "testPassword";
    String key = "testKey";
    try (MockedConstruction<FideliusClient> ignored =
        Mockito.mockConstruction(
            FideliusClient.class,
            (mock, context) ->
                when(mock.getCredential(any(), any(), any(), any(), any())).thenReturn(expected))) {
      assertEquals(expected, Util.getPassword(key, StringUtils.EMPTY));
      assertEquals(
          String.format("Successfully retrieved password of %s from Fidelius", key),
          appender.getEvents().get(0).getFormattedMessage());
    }
  }

  @Test
  public void testGetPassword_whenFail() {
    Appender appender = TestUtil.setUpLogMonitorForClass(Util.class);
    String expected = StringUtils.EMPTY;
    String key = "testKey";
    try (MockedConstruction<FideliusClient> ignored =
        Mockito.mockConstruction(
            FideliusClient.class,
            (mock, context) ->
                when(mock.getCredential(any(), any(), any(), any(), any())).thenReturn(expected))) {
      assertEquals(expected, Util.getPassword(key, StringUtils.EMPTY));
      assertEquals(
          String.format("Failed to retrieve password of %s from Fidelius", key),
          appender.getEvents().get(0).getFormattedMessage());
    }
  }

  @Test
  public void testGetPassword_whenFail2() {
    Appender appender = TestUtil.setUpLogMonitorForClass(Util.class);
    String expected = null;
    String key = "testKey";
    try (MockedConstruction<FideliusClient> ignored =
        Mockito.mockConstruction(
            FideliusClient.class,
            (mock, context) ->
                when(mock.getCredential(any(), any(), any(), any(), any())).thenReturn(expected))) {
      assertEquals(expected, Util.getPassword(key, StringUtils.EMPTY));
      assertEquals(
          String.format("Failed to retrieve password of %s from Fidelius", key),
          appender.getEvents().get(0).getFormattedMessage());
    }
  }

  @Test
  public void testGetPassword_whenException() {
    String key = "testKey";
    try (MockedConstruction<FideliusClient> ignored =
        Mockito.mockConstruction(
            FideliusClient.class,
            (mock, context) ->
                when(mock.getCredential(any(), any(), any(), any(), any()))
                    .thenThrow(new RuntimeException("exception")))) {
      assertThrows(RuntimeException.class, () -> Util.getPassword(key, StringUtils.EMPTY));
    }
  }

  @Test
  public void testGenerateS3Location() {
    String expected =
        "s3://4652-5751-2377-datamgt-rmcs-kms/rmcs-apibi/dapi/billing/jsonl/rmcs-apibi-receivables-in/schm-v0/data-v0/upload-dt-2023-03-15/uuid-920b633d-3f43-479c-842a-631ff319a65f/apibi_2023-03-15T11-50-00-246Z.jsonl";
    assertEquals(expected, Util.generateS3Location(TestUtil.getBusinessObjectData(true)));
  }

  @Test
  public void testGenerateS3Location_ExceptionOccurred() {
    BusinessObjectData businessObjectData = TestUtil.getBusinessObjectData(false);
    Throwable exception =
        assertThrows(
            RuntimeException.class, () -> Util.generateS3Location(businessObjectData));
    assertEquals("Invalid filePath or BucketName", exception.getMessage());
  }

  @Test
  public void testGenerateTransmissionId() {
    String expected =
        "2023-03-15/D920b633d-3f43-479c-842a-631ff319a65f/apibi_2023-03-15T11-50-00-246Z.jsonl";
    BusinessObjectData businessObjectData = TestUtil.getBusinessObjectData(true);
    assertEquals(expected, Util.generateTransmissionId(businessObjectData));
  }

  @Test
  public void testGetRevenueStreamFromNameSpace() {
    BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent =
        new BusinessObjectDataStatusChangeEvent();
    BusinessObjectDataKey businessObjectDataKey = new BusinessObjectDataKey();
    businessObjectDataKey.setNamespace("RMCS-APIBI");
    businessObjectDataStatusChangeEvent.setBusinessObjectDataKey(businessObjectDataKey);
    assertEquals(
        "APIBI", Util.getRevenueStreamFromNameSpace(businessObjectDataStatusChangeEvent));
  }

  @Test
  public void testGetMetaDataByKey() {
    BusinessObjectData businessObjectData = TestUtil.getBusinessObjectData(true);
    assertEquals(
        "10", Util.getMetaDataByKey(businessObjectData, Constants.META_DATA_KEY_JSON_LINE_COUNT));
  }

  @Test
  public void testGetMetaDataByKey_noKey() {
    BusinessObjectData businessObjectData = TestUtil.getBusinessObjectData(false);
    assertEquals(
        StringUtils.EMPTY,
        Util.getMetaDataByKey(businessObjectData, Constants.META_DATA_KEY_JSON_LINE_COUNT));
  }
}
