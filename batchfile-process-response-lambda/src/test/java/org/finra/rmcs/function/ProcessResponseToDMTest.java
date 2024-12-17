package org.finra.rmcs.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.entity.ReceivableJsonFileEntity;
import org.finra.rmcs.exception.JsonLineCountMisMatchException;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;
import org.finra.rmcs.service.impl.ProcessResponseServiceImpl;
import org.finra.rmcs.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class ProcessResponseToDMTest {

  @InjectMocks
  private ProcessResponseToDM processResponseToDM;

  @Mock
  private ProcessResponseServiceImpl processResponseService;

  @Test
  public void testProcessResponse() throws Exception {
    Map<String, Object> map = new HashMap<>();
    map.put(Constants.TYPE_KEY, Constants.STATUS_COMPLETED);
    map.put(Constants.TRANSMISSION_ID_KEY, "testTransmissionId");
    map.put(Constants.FILE_URL_KEY, "testS3Url");
    map.put(Constants.META_DATA_KEY_JSON_LINE_COUNT, "10");
    map.put(Constants.SNS_MESSAGE_ID_KEY, "testSNSId");
    ReceivableJsonFileEntity receivableJsonFileEntity = new ReceivableJsonFileEntity();
    receivableJsonFileEntity.setDmDataStatusChangeEvent(
        TestUtil.getResourceFileContents("/ValidBizObjChangeEvent.json"));
    Mockito.when(processResponseService.getReceivableJsonEntityByTransmissionId(anyString()))
        .thenReturn(receivableJsonFileEntity);
    Map<String, String> responseFile = new HashMap<>();
    responseFile.put("fatal", "testContent");
    Mockito.when(
            processResponseService.createResponseFile(
                anyString(),
                anyString(),
                anyString(),
                any(ReceivableJsonFileEntity.class),
                any(BusinessObjectDataStatusChangeEvent.class)))
        .thenReturn(responseFile);
    Mockito.doNothing()
        .when(processResponseService)
        .registerResponseFileToDM(
            anyString(), anyString(), any(), any(BusinessObjectDataStatusChangeEvent.class));
    Map<String, Object> returnMap = processResponseToDM.apply(map);
    assertEquals(Constants.STATUS_COMPLETED, returnMap.get(Constants.TYPE_KEY));
    Mockito.verify(processResponseService, Mockito.times(0))
        .sendErrorNotificationEmail(anyString(), anyString(), anyString(), anyString());
  }

  @Test
  public void testProcessResponse_whenFailedValidation_thenSendingEmail() throws Exception {
    Map<String, Object> map = new HashMap<>();
    map.put(Constants.TYPE_KEY, Constants.STATUS_FATAL);
    map.put(Constants.TRANSMISSION_ID_KEY, "testTransmissionId");
    map.put(Constants.FILE_URL_KEY, "testS3Url");
    map.put(Constants.META_DATA_KEY_JSON_LINE_COUNT, "10");
    map.put(Constants.SNS_MESSAGE_ID_KEY, "testSNSId");
    ReceivableJsonFileEntity receivableJsonFileEntity = new ReceivableJsonFileEntity();
    receivableJsonFileEntity.setDmDataStatusChangeEvent(
        TestUtil.getResourceFileContents("/ValidBizObjChangeEvent.json"));
    Mockito.when(processResponseService.getReceivableJsonEntityByTransmissionId(anyString()))
        .thenReturn(receivableJsonFileEntity);
    String expectedMsg = "json_line_count is not match with the response file";
    Mockito.when(
            processResponseService.createResponseFile(
                anyString(),
                anyString(),
                anyString(),
                any(ReceivableJsonFileEntity.class),
                any(BusinessObjectDataStatusChangeEvent.class)))
        .thenThrow(new JsonLineCountMisMatchException(expectedMsg));
    Throwable exception =
        assertThrows(
            JsonLineCountMisMatchException.class, () -> processResponseToDM.apply(map));
    assertEquals(expectedMsg, exception.getMessage());
    Mockito.verify(processResponseService)
        .sendErrorNotificationEmail(any(Exception.class), anyString(), anyString(), anyString());
  }

  @Test
  public void testProcessResponse_whenUnExpectedException_thenSendingEmail() throws Exception {
    Map<String, Object> map = new HashMap<>();
    map.put(Constants.TYPE_KEY, Constants.STATUS_FATAL);
    map.put(Constants.TRANSMISSION_ID_KEY, "testTransmissionId");
    map.put(Constants.FILE_URL_KEY, "testS3Url");
    map.put(Constants.META_DATA_KEY_JSON_LINE_COUNT, "10");
    map.put(Constants.SNS_MESSAGE_ID_KEY, "testSNSId");
    ReceivableJsonFileEntity receivableJsonFileEntity = new ReceivableJsonFileEntity();
    receivableJsonFileEntity.setDmDataStatusChangeEvent(
        TestUtil.getResourceFileContents("/ValidBizObjChangeEvent.json"));
    Mockito.when(processResponseService.getReceivableJsonEntityByTransmissionId(anyString()))
        .thenReturn(receivableJsonFileEntity);
    Map<String, String> responseFile = new HashMap<>();
    responseFile.put(Constants.STATUS_FATAL, StringUtils.EMPTY);
    Mockito.when(
            processResponseService.createResponseFile(
                anyString(),
                anyString(),
                anyString(),
                any(ReceivableJsonFileEntity.class),
                any(BusinessObjectDataStatusChangeEvent.class)))
        .thenReturn(responseFile);
    Throwable exception =
        assertThrows(RuntimeException.class, () -> processResponseToDM.apply(map));
    assertTrue(exception.getMessage().contains("Response File is empty for file"));
    Mockito.verify(processResponseService)
        .sendErrorNotificationEmail(any(Exception.class), anyString(), anyString(), anyString());
  }

  @Test
  public void testProcessResponse_whenFatalEvent_thenSendingEmail() throws Exception {
    Map<String, Object> map = new HashMap<>();
    map.put(Constants.TYPE_KEY, Constants.STATUS_FATAL);
    map.put(Constants.TRANSMISSION_ID_KEY, "testTransmissionId");
    map.put(Constants.FILE_URL_KEY, "testS3Url");
    map.put(Constants.META_DATA_KEY_JSON_LINE_COUNT, "10");
    map.put(Constants.SNS_MESSAGE_ID_KEY, "testSNSId");
    ReceivableJsonFileEntity receivableJsonFileEntity = new ReceivableJsonFileEntity();
    receivableJsonFileEntity.setDmDataStatusChangeEvent(
        TestUtil.getResourceFileContents("/ValidBizObjChangeEvent.json"));
    receivableJsonFileEntity.setMessage("testMsg");
    Mockito.when(processResponseService.getReceivableJsonEntityByTransmissionId(anyString()))
        .thenReturn(receivableJsonFileEntity);
    Map<String, String> responseFile = new HashMap<>();
    responseFile.put("fatal", "testContent");
    Mockito.when(
            processResponseService.createResponseFile(
                anyString(),
                anyString(),
                anyString(),
                any(ReceivableJsonFileEntity.class),
                any(BusinessObjectDataStatusChangeEvent.class)))
        .thenReturn(responseFile);
    Mockito.doNothing()
        .when(processResponseService)
        .registerResponseFileToDM(
            anyString(), anyString(), any(), any(BusinessObjectDataStatusChangeEvent.class));
    Map<String, Object> returnMap = processResponseToDM.apply(map);
    assertEquals(Constants.STATUS_COMPLETED, returnMap.get(Constants.TYPE_KEY));
    Mockito.verify(processResponseService)
        .sendErrorNotificationEmail(anyString(), anyString(), anyString(), anyString());
  }
}
