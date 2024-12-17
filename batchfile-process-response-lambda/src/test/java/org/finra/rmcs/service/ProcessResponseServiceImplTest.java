package org.finra.rmcs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.finra.herd.sdk.model.BusinessObjectData;
import org.finra.herd.sdk.model.BusinessObjectFormat;
import org.finra.rmcs.common.service.email.EmailServiceImpl;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.constants.HerdEntity;
import org.finra.rmcs.entity.ReceivableJsonFileEntity;
import org.finra.rmcs.exception.JsonLineCountMisMatchException;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;
import org.finra.rmcs.repository.ReceivableJsonRepository;
import org.finra.rmcs.repository.ReceivableReqRepository;
import org.finra.rmcs.service.impl.HerdServiceImpl;
import org.finra.rmcs.service.impl.ProcessResponseServiceImpl;
import org.finra.rmcs.util.TestUtil;
import org.finra.rmcs.util.Util;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

@SpringJUnitConfig
public class ProcessResponseServiceImplTest {

  @InjectMocks
  private ProcessResponseServiceImpl processResponseService;

  @Mock
  private ReceivableJsonRepository receivableJsonRepository;

  @Mock
  private ReceivableReqRepository receivableReqRepository;

  @Mock
  private HerdServiceImpl herdService;

  @Mock
  private EmailServiceImpl emailService;

  private MockedStatic<Util> mockStatic;

  @BeforeEach
  public void before() {
    mockStatic = Mockito.mockStatic(Util.class);
    ReflectionTestUtils.setField(
        processResponseService,
        "errorEmailSubject",
        "[%s] - Error happened in processing batch file");
    ReflectionTestUtils.setField(
        processResponseService,
        "errorEmailBody",
        "%s happened an error in processing batch file due to the following issue, please investigate it. Thanks!\\n\\nerror_message: %s\\n\\ntransmission_id: %s\\nsns_message_id: %s\\ns3_url: %s\\nstack trace: \\n%s");
    ReflectionTestUtils.setField(processResponseService, "errorEmailFrom", "test@finra.org");
    ReflectionTestUtils.setField(processResponseService, "errorEmailTo", "test@finra.org");
    ReflectionTestUtils.setField(processResponseService, "errorEmailCC", "test@finra.org");
  }

  @AfterEach
  public void after() {
    mockStatic.close();
  }

  @Test
  public void testCreateResponseFile_forFatalFile() {
    String expectedKey = "apibi_2023-03-15T11-50-00-246Z_fatal.jsonl";
    String expectedValue = "testFatalFile";
    ReceivableJsonFileEntity receivableJsonFileEntity = new ReceivableJsonFileEntity();
    receivableJsonFileEntity.setMessage(expectedValue);
    Mockito.when(
            herdService.retrieveBusinessObjectData(any(BusinessObjectDataStatusChangeEvent.class)))
        .thenReturn(new BusinessObjectData());
    Mockito.when(Util.generateResponseFileName(anyString(), any(BusinessObjectData.class)))
        .thenReturn(expectedKey);
    Map<String, String> responseFile =
        processResponseService.createResponseFile(
            "fatal",
            "transmissionId",
            "10",
            receivableJsonFileEntity,
            new BusinessObjectDataStatusChangeEvent());
    assertEquals(1, responseFile.size());
    assertTrue(responseFile.containsKey(expectedKey));
    assertEquals(expectedValue, responseFile.get(expectedKey));
  }

  @Test
  public void testCreateResponseFile_forOKFailFile() {
    String expectedOKKey = "apibi_2023-03-15T11-50-00-246Z_ok.jsonl";
    String expectedFailKey = "apibi_2023-03-15T11-50-00-246Z_fail.jsonl";
    BusinessObjectData businessObjectData = new BusinessObjectData();
    Mockito.when(
            receivableReqRepository.findResponsePayloadByTransmissionIdAndStatus(
                anyString(), anyString()))
        .thenReturn(Arrays.asList("OK1", "OK2", "OK3", "OK4", "OK5", "OK6", "OK7"))
        .thenReturn(Arrays.asList("FAIL1", "FAIL2", "FAIL3"));
    Mockito.when(
            herdService.retrieveBusinessObjectData(any(BusinessObjectDataStatusChangeEvent.class)))
        .thenReturn(businessObjectData);
    Mockito.when(Util.generateResponseFileName(anyString(), any(BusinessObjectData.class)))
        .thenReturn(expectedOKKey)
        .thenReturn(expectedFailKey);
    Map<String, String> responseFile =
        processResponseService.createResponseFile(
            "completed",
            "transmissionId",
            "10",
            new ReceivableJsonFileEntity(),
            new BusinessObjectDataStatusChangeEvent());
    assertEquals(2, responseFile.size());
    assertTrue(responseFile.containsKey(expectedOKKey));
    assertTrue(responseFile.containsKey(expectedFailKey));
    assertEquals("OK1\nOK2\nOK3\nOK4\nOK5\nOK6\nOK7", responseFile.get(expectedOKKey));
    assertEquals("FAIL1\nFAIL2\nFAIL3", responseFile.get(expectedFailKey));
  }

  @Test
  public void testCreateResponseFile_forOnlyOKFile() {
    String expectedOKKey = "apibi_2023-03-15T11-50-00-246Z_ok.jsonl";
    BusinessObjectData businessObjectData = new BusinessObjectData();
    Mockito.when(
            receivableReqRepository.findResponsePayloadByTransmissionIdAndStatus(
                anyString(), anyString()))
        .thenReturn(Arrays.asList("OK1", "OK2"))
        .thenReturn(Collections.emptyList());
    Mockito.when(
            herdService.retrieveBusinessObjectData(any(BusinessObjectDataStatusChangeEvent.class)))
        .thenReturn(businessObjectData);
    Mockito.when(Util.generateResponseFileName(anyString(), any(BusinessObjectData.class)))
        .thenReturn(expectedOKKey);
    Map<String, String> responseFile =
        processResponseService.createResponseFile(
            "completed",
            "transmissionId",
            "2",
            new ReceivableJsonFileEntity(),
            new BusinessObjectDataStatusChangeEvent());
    assertEquals(1, responseFile.size());
    assertTrue(responseFile.containsKey(expectedOKKey));
    assertEquals("OK1\nOK2", responseFile.get(expectedOKKey));
  }

  @Test
  public void testCreateResponseFile_forOnlyFailFile() {
    String expectedFailKey = "apibi_2023-03-15T11-50-00-246Z_fail.jsonl";
    BusinessObjectData businessObjectData = new BusinessObjectData();
    Mockito.when(
            receivableReqRepository.findResponsePayloadByTransmissionIdAndStatus(
                anyString(), anyString()))
        .thenReturn(Collections.emptyList())
        .thenReturn(Arrays.asList("FAIL1", "FAIL2"));
    Mockito.when(
            herdService.retrieveBusinessObjectData(any(BusinessObjectDataStatusChangeEvent.class)))
        .thenReturn(businessObjectData);
    Mockito.when(Util.generateResponseFileName(anyString(), any(BusinessObjectData.class)))
        .thenReturn(expectedFailKey);
    Map<String, String> responseFile =
        processResponseService.createResponseFile(
            "completed",
            "transmissionId",
            "2",
            new ReceivableJsonFileEntity(),
            new BusinessObjectDataStatusChangeEvent());
    assertEquals(1, responseFile.size());
    assertTrue(responseFile.containsKey(expectedFailKey));
    assertEquals("FAIL1\nFAIL2", responseFile.get(expectedFailKey));
  }

  @Test
  public void
  testCreateResponseFile_whenJsonLineCountMisMatch_thenThrowJsonLineCountMisMatchException() {
    String expectedFailKey = "apibi_2023-03-15T11-50-00-246Z_fail.jsonl";
    BusinessObjectData businessObjectData = new BusinessObjectData();
    Mockito.when(
            receivableReqRepository.findResponsePayloadByTransmissionIdAndStatus(
                anyString(), anyString()))
        .thenReturn(Arrays.asList("OK1", "OK2", "OK3", "OK4", "OK5", "OK6"))
        .thenReturn(Arrays.asList("FAIL1", "FAIL2", "FAIL3", "FAIL4"));
    Mockito.when(
            herdService.retrieveBusinessObjectData(any(BusinessObjectDataStatusChangeEvent.class)))
        .thenReturn(businessObjectData);
    Mockito.when(Util.generateResponseFileName(anyString(), any(BusinessObjectData.class)))
        .thenReturn(expectedFailKey);
    Throwable exception =
        assertThrows(
            JsonLineCountMisMatchException.class,
            () ->
                processResponseService.createResponseFile(
                    "completed",
                    "transmissionId",
                    "9",
                    new ReceivableJsonFileEntity(),
                    new BusinessObjectDataStatusChangeEvent()));
    assertEquals(
        "json_line_count is not match with the response file for transmission_id: transmissionId, expected: 9, actual: 10",
        exception.getMessage());
  }

  @Test
  public void testRegisterResponseFileToDM() {
    BusinessObjectData businessObjectData = TestUtil.getBusinessObjectData(true);
    Mockito.when(
            herdService.getBusinessObjectFormat(
                any(BusinessObjectDataStatusChangeEvent.class), any(HerdEntity.class)))
        .thenReturn(new BusinessObjectFormat());
    Mockito.when(
            herdService.preRegisterBusinessObjectData(
                any(BusinessObjectFormat.class), any(BusinessObjectDataStatusChangeEvent.class)))
        .thenReturn(businessObjectData);
    Mockito.when(
            herdService.uploadResponseFileToS3(
                any(BusinessObjectData.class), anyString(), anyString()))
        .thenReturn("s3Key");
    Mockito.doNothing()
        .when(herdService)
        .updateBusinessObjectDataStorage(any(BusinessObjectData.class));
    Mockito.when(
            herdService.updateBusinessObjectDataStatus(any(BusinessObjectData.class), anyString()))
        .thenReturn(Constants.DM_STATUS_VALID);
    processResponseService.registerResponseFileToDM(
        "testFileName",
        "testFileContent",
        HerdEntity.FATAL,
        new BusinessObjectDataStatusChangeEvent());
    Mockito.verify(herdService)
        .getBusinessObjectFormat(
            any(BusinessObjectDataStatusChangeEvent.class), any(HerdEntity.class));
    Mockito.verify(herdService)
        .preRegisterBusinessObjectData(
            any(BusinessObjectFormat.class), any(BusinessObjectDataStatusChangeEvent.class));
    Mockito.verify(herdService)
        .uploadResponseFileToS3(any(BusinessObjectData.class), anyString(), anyString());
    Mockito.verify(herdService).updateBusinessObjectDataStorage(any(BusinessObjectData.class));
    Mockito.verify(herdService)
        .updateBusinessObjectDataStatus(any(BusinessObjectData.class), anyString());
  }

  @Test
  public void testRegisterResponseFileToDM_somethingWrongAndInvalidBizObj() {
    BusinessObjectData businessObjectData = TestUtil.getBusinessObjectData(true);
    String expected = "testException";
    Mockito.when(
            herdService.getBusinessObjectFormat(
                any(BusinessObjectDataStatusChangeEvent.class), any(HerdEntity.class)))
        .thenReturn(new BusinessObjectFormat());
    Mockito.when(
            herdService.preRegisterBusinessObjectData(
                any(BusinessObjectFormat.class), any(BusinessObjectDataStatusChangeEvent.class)))
        .thenReturn(businessObjectData);
    Mockito.when(herdService.uploadResponseFileToS3(any(), anyString(), anyString()))
        .thenThrow(new RuntimeException(expected));
    Mockito.when(
            herdService.updateBusinessObjectDataStatus(any(BusinessObjectData.class), anyString()))
        .thenReturn(Constants.DM_STATUS_INVALID);
    Throwable exception =
        assertThrows(
            RuntimeException.class,
            () ->
                processResponseService.registerResponseFileToDM(
                    "testFileName",
                    "testFileContent",
                    HerdEntity.FATAL,
                    new BusinessObjectDataStatusChangeEvent()));
    Mockito.verify(herdService)
        .getBusinessObjectFormat(
            any(BusinessObjectDataStatusChangeEvent.class), any(HerdEntity.class));
    Mockito.verify(herdService)
        .preRegisterBusinessObjectData(
            any(BusinessObjectFormat.class), any(BusinessObjectDataStatusChangeEvent.class));
    assertTrue(exception.getMessage().contains(expected));
  }

  @Test
  public void testRegisterResponseFileToDM_whenException1() {
    BusinessObjectData businessObjectData = TestUtil.getBusinessObjectData(true);
    Mockito.when(
            herdService.getBusinessObjectFormat(
                any(BusinessObjectDataStatusChangeEvent.class), any(HerdEntity.class)))
        .thenReturn(new BusinessObjectFormat());
    Mockito.when(
            herdService.preRegisterBusinessObjectData(
                any(BusinessObjectFormat.class), any(BusinessObjectDataStatusChangeEvent.class)))
        .thenReturn(businessObjectData);
    Mockito.when(
            herdService.uploadResponseFileToS3(
                any(BusinessObjectData.class), anyString(), anyString()))
        .thenReturn("s3Key");
    Mockito.doNothing()
        .when(herdService)
        .updateBusinessObjectDataStorage(any(BusinessObjectData.class));
    Mockito.when(
            herdService.updateBusinessObjectDataStatus(any(BusinessObjectData.class), anyString()))
        .thenReturn("testStatus");
    Throwable exception =
        assertThrows(
            RuntimeException.class,
            () ->
                processResponseService.registerResponseFileToDM(
                    "testFileName",
                    "testFileContent",
                    HerdEntity.FATAL,
                    new BusinessObjectDataStatusChangeEvent()));
    Mockito.verify(herdService)
        .getBusinessObjectFormat(
            any(BusinessObjectDataStatusChangeEvent.class), any(HerdEntity.class));
    Mockito.verify(herdService)
        .preRegisterBusinessObjectData(
            any(BusinessObjectFormat.class), any(BusinessObjectDataStatusChangeEvent.class));
    Mockito.verify(herdService)
        .uploadResponseFileToS3(any(BusinessObjectData.class), anyString(), anyString());
    Mockito.verify(herdService).updateBusinessObjectDataStorage(any(BusinessObjectData.class));
    Mockito.verify(herdService, times((2)))
        .updateBusinessObjectDataStatus(any(BusinessObjectData.class), anyString());
    assertEquals(
        "Failed to invalidate business object data status for businessObjectDefinitionName RMCS-APIBI-RECEIVABLES-IN, partitionValue 2023-03-15, subPartitionValue D920b633d-3f43-479c-842a-631ff319a65f",
        exception.getMessage());
  }

  @Test
  public void testRegisterResponseFileToDM_whenException2() {
    Mockito.when(
            herdService.getBusinessObjectFormat(
                any(BusinessObjectDataStatusChangeEvent.class), any(HerdEntity.class)))
        .thenReturn(new BusinessObjectFormat());
    Mockito.when(
            herdService.preRegisterBusinessObjectData(
                any(BusinessObjectFormat.class), any(BusinessObjectDataStatusChangeEvent.class)))
        .thenThrow(
            new RuntimeException(
                "Pre-registration of business object data for RMCS-APIBI-RECEIVABLES-OUT-FATAL failed!"));
    Throwable exception =
        assertThrows(
            RuntimeException.class,
            () ->
                processResponseService.registerResponseFileToDM(
                    "testFileName",
                    "testFileContent",
                    HerdEntity.FATAL,
                    new BusinessObjectDataStatusChangeEvent()));
    Mockito.verify(herdService)
        .getBusinessObjectFormat(
            any(BusinessObjectDataStatusChangeEvent.class), any(HerdEntity.class));
    Mockito.verify(herdService)
        .preRegisterBusinessObjectData(
            any(BusinessObjectFormat.class), any(BusinessObjectDataStatusChangeEvent.class));
    assertTrue(
        exception
            .getMessage()
            .contains(
                "Pre-registration of business object data for RMCS-APIBI-RECEIVABLES-OUT-FATAL failed!"))
    ;
  }

  @Test
  public void testGetReceivableJsonEntityByTransmissionId() {
    String expected = "testMessage";
    ReceivableJsonFileEntity receivableJsonFileEntity = new ReceivableJsonFileEntity();
    receivableJsonFileEntity.setMessage(expected);
    Mockito.when(receivableJsonRepository.findByTransmissionId(anyString()))
        .thenReturn(Optional.of(receivableJsonFileEntity));
    ReceivableJsonFileEntity actual =
        processResponseService.getReceivableJsonEntityByTransmissionId("testTransmissionId");
    Mockito.verify(receivableJsonRepository).findByTransmissionId(anyString());
    assertEquals(expected, actual.getMessage());
  }

  @Test
  public void testGetReceivableJsonEntityByTransmissionId_whenNoEntity() {
    Mockito.when(receivableJsonRepository.findByTransmissionId(anyString()))
        .thenReturn(Optional.empty());
    Throwable exception =
        assertThrows(
            RuntimeException.class,
            () ->
                processResponseService.getReceivableJsonEntityByTransmissionId(
                    "testTransmissionId"));
    Mockito.verify(receivableJsonRepository).findByTransmissionId(anyString());
    assertEquals(
        "ReceivableJsonEntity is null for transmission_id: testTransmissionId",
        exception.getMessage());
  }

  @Test
  public void testUpdateReceivableJsonEntityStatus() {
    processResponseService.updateReceivableJsonEntityStatus(
        new ReceivableJsonFileEntity(), Map.of("testKey", "testValue"), "testStatus");
    Mockito.verify(receivableJsonRepository).save(any(ReceivableJsonFileEntity.class));
  }

  @Test
  public void testSendErrorNotificationEmail_whenJsonLineCountMisMatchException() {
    processResponseService.sendErrorNotificationEmail(
        new JsonLineCountMisMatchException("testException"), "testS3", "testSNS", "testId");
    verify(emailService).sendEMail(anyString(), anyString(), anyString(), anyString(), anyString());
  }

  @Test
  public void testSendErrorNotificationEmail_whenUnexpectedException() {
    processResponseService.sendErrorNotificationEmail(
        new RuntimeException("testException"), "testS3", "testSNS", "testId");
    verify(emailService).sendEMail(anyString(), anyString(), anyString(), anyString(), anyString());
  }
}
