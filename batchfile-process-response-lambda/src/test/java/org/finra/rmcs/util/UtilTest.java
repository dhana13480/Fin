package org.finra.rmcs.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.finra.fidelius.FideliusClient;
import org.finra.herd.sdk.model.BusinessObjectData;
import org.finra.herd.sdk.model.BusinessObjectDataKey;
import org.finra.herd.sdk.model.StorageFile;
import org.finra.herd.sdk.model.StorageUnit;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;
import org.finra.rmcs.util.TestUtil.Appender;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

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
  public void testGenerateResponseFileName() {
    String expected = "apibi_2023-03-15T11-50-00-246Z_fatal.jsonl";
    List<StorageUnit> storageUnits = new ArrayList<>();
    List<StorageFile> storageFiles = new ArrayList<>();
    StorageFile storageFile = new StorageFile();
    storageFile.setFilePath(
        "rmcs-apibi/dapi/billing/jsonl/rmcs-apibi-receivables-in/schm-v0/data-v0/upload-dt%3D2023-03-15/uuid%3D920b633d-3f43-479c-842a-631ff319a65f/apibi_2023-03-15T11-50-00-246Z.jsonl");
    storageFiles.add(storageFile);
    StorageUnit storageUnit = new StorageUnit();
    storageUnit.setStorageFiles(storageFiles);
    storageUnits.add(storageUnit);
    BusinessObjectData businessObjectData = new BusinessObjectData();
    businessObjectData.setStorageUnits(storageUnits);
    assertEquals(
        expected, Util.generateResponseFileName(Constants.STATUS_FATAL, businessObjectData));
  }
}
