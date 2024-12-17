package org.finra.rmcs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.finra.herd.sdk.model.BusinessObjectData;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.entity.ReceivableJsonFileEntity;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;
import org.finra.rmcs.repository.ReceivableJsonFileRepository;
import org.finra.rmcs.service.impl.ProcessDmReceivableFileServiceImpl;
import org.finra.rmcs.service.impl.S3ServiceImpl;
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
public class ProcessDmReceivableFileServiceImplTest {

  @InjectMocks
  private ProcessDmReceivableFileServiceImpl processDmReceivableFileService;

  @Mock
  private ReceivableJsonFileRepository receivableJsonFileRepository;

  @Mock
  private S3ServiceImpl s3Service;

  private MockedStatic<Util> mockStatic;

  @BeforeEach
  public void init() throws Exception {
    ReflectionTestUtils.setField(
        processDmReceivableFileService, "objectMapper", new ObjectMapper());
  }

  @BeforeEach
  public void before() {
    mockStatic = Mockito.mockStatic(Util.class);
  }

  @AfterEach
  public void after() {
    mockStatic.close();
  }

  @Test
  public void testUpsert_happyPath() {
    Mockito.when(receivableJsonFileRepository.save(any()))
        .thenReturn(new ReceivableJsonFileEntity());
    Mockito.when(receivableJsonFileRepository.findByTransmissionId(anyString()))
        .thenReturn(Optional.empty());
    Map<String, Object> returnMap = new HashMap<>();
    processDmReceivableFileService.upsertEntry(
        "id", "s3Url", "revenueStream", new BusinessObjectDataStatusChangeEvent(), returnMap);
    Mockito.verify(receivableJsonFileRepository).save(any());
    assertFalse((Boolean) returnMap.get(Constants.RE_RUN_KEY));
  }

  @Test
  public void testUpsert_rerun() {
    Mockito.when(receivableJsonFileRepository.save(any()))
        .thenReturn(new ReceivableJsonFileEntity());
    ReceivableJsonFileEntity receivableJsonFileEntity = new ReceivableJsonFileEntity();
    receivableJsonFileEntity.setVersion(0);
    Mockito.when(receivableJsonFileRepository.findByTransmissionId(anyString()))
        .thenReturn(Optional.of(receivableJsonFileEntity));
    Map<String, Object> returnMap = new HashMap<>();
    processDmReceivableFileService.upsertEntry(
        "id", "s3Url", "revenueStream", new BusinessObjectDataStatusChangeEvent(), returnMap);
    Mockito.verify(receivableJsonFileRepository).save(any());
    assertTrue((Boolean) returnMap.get(Constants.RE_RUN_KEY));
  }

  @Test
  public void testUpsertEntry_nullReceivableJsonEntity() throws Exception {
    Mockito.when(receivableJsonFileRepository.findByTransmissionId(any()))
        .thenReturn(Optional.ofNullable(null));
    Map<String, Object> returnMap = new HashMap<>();
    processDmReceivableFileService.upsertEntry(
        "id", "s3Url", "revenueStream", new BusinessObjectDataStatusChangeEvent(), returnMap);
    Mockito.verify(receivableJsonFileRepository).save(any());
    assertFalse((Boolean) returnMap.get(Constants.RE_RUN_KEY));
  }

  @Test
  public void testValidateReceivableFile_happyPath() throws Exception {
    Mockito.when(s3Service.getFileSize(anyString())).thenReturn(256L);
    Mockito.when(Util.getMetaDataByKey(any(BusinessObjectData.class), anyString()))
        .thenReturn("APIBI")
        .thenReturn("10");
    String jsonl = TestUtil.getResourceFileContents("/receivables.jsonl");
    Mockito.when(s3Service.retrieveS3Object(anyString())).thenReturn(jsonl);
    Map<String, Object> returnMap = new HashMap<>();
    processDmReceivableFileService.validateReceivableFile(
        "", "", "APIBI", new BusinessObjectData(), returnMap);
    verify(receivableJsonFileRepository, times(0)).save(any());
    assertEquals(Constants.COMPLETED, returnMap.get(Constants.TYPE));
  }

  @Test
  public void testValidateReceivableFile_fileSizeExceed() {
    Mockito.when(s3Service.getFileSize(anyString())).thenReturn(2247483648L);
    Mockito.when(receivableJsonFileRepository.findByTransmissionId(anyString()))
        .thenReturn(Optional.of(new ReceivableJsonFileEntity()));
    Mockito.when(receivableJsonFileRepository.save(any()))
        .thenReturn(new ReceivableJsonFileEntity());
    Map<String, Object> returnMap = new HashMap<>();
    processDmReceivableFileService.validateReceivableFile(
        "", "", "APIBI", new BusinessObjectData(), returnMap);
    mockStatic.verify(
        () -> Util.getMetaDataByKey(any(BusinessObjectData.class), anyString()), times(0));
    verify(receivableJsonFileRepository).save(any());
    assertEquals(Constants.FATAL, returnMap.get(Constants.TYPE));
  }

  @Test
  public void testValidateReceivableFile_metadataMismatch() throws Exception {
    Mockito.when(s3Service.getFileSize(anyString())).thenReturn(256L);
    Mockito.when(Util.getMetaDataByKey(any(BusinessObjectData.class), anyString()))
        .thenReturn("ADVRG")
        .thenReturn("9");
    String jsonl = TestUtil.getResourceFileContents("/receivables.jsonl");
    Mockito.when(s3Service.retrieveS3Object(anyString())).thenReturn(jsonl);
    Mockito.when(receivableJsonFileRepository.findByTransmissionId(anyString()))
        .thenReturn(Optional.of(new ReceivableJsonFileEntity()));
    Mockito.when(receivableJsonFileRepository.save(any()))
        .thenReturn(new ReceivableJsonFileEntity());
    Map<String, Object> returnMap = new HashMap<>();
    processDmReceivableFileService.validateReceivableFile(
        "", "", "APIBI", new BusinessObjectData(), returnMap);
    mockStatic.verify(
        () -> Util.getMetaDataByKey(any(BusinessObjectData.class), anyString()), times(2));
    verify(receivableJsonFileRepository).save(any());
    assertEquals(Constants.FATAL, returnMap.get(Constants.TYPE));
  }

  @Test
  public void testValidateReceivableFile_metadataMismatch_noEntryToUpdate() throws Exception {
    Mockito.when(s3Service.getFileSize(anyString())).thenReturn(256L);
    Mockito.when(Util.getMetaDataByKey(any(BusinessObjectData.class), anyString()))
        .thenReturn("ADVRG")
        .thenReturn("9");
    String jsonl = TestUtil.getResourceFileContents("/receivables.jsonl");
    Mockito.when(s3Service.retrieveS3Object(anyString())).thenReturn(jsonl);
    Mockito.when(receivableJsonFileRepository.findByTransmissionId(anyString()))
        .thenReturn(Optional.empty());
    Map<String, Object> returnMap = new HashMap<>();
    Throwable exception =
        assertThrows(
            RuntimeException.class,
            () ->
                processDmReceivableFileService.validateReceivableFile(
                    "testTransmissionId", "", "APIBI", new BusinessObjectData(), returnMap));
    mockStatic.verify(
        () -> Util.getMetaDataByKey(any(BusinessObjectData.class), anyString()), times(2));
    Mockito.verify(receivableJsonFileRepository, times(0))
        .save(any(ReceivableJsonFileEntity.class));
    assertEquals(
        "Invalid Transaction as no transmission_id testTransmissionId found",
        exception.getMessage());
  }

  @Test
  public void testValidateReceivableFile_jsonFormatError() {
    Mockito.when(s3Service.getFileSize(anyString())).thenReturn(256L);
    Mockito.when(Util.getMetaDataByKey(any(BusinessObjectData.class), anyString()))
        .thenReturn("ADVRG")
        .thenReturn("9");
    Mockito.when(s3Service.retrieveS3Object(anyString()))
        .thenReturn("test\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest");
    Mockito.when(receivableJsonFileRepository.findByTransmissionId(anyString()))
        .thenReturn(Optional.of(new ReceivableJsonFileEntity()));
    Mockito.when(receivableJsonFileRepository.save(any()))
        .thenReturn(new ReceivableJsonFileEntity());
    Map<String, Object> returnMap = new HashMap<>();
    processDmReceivableFileService.validateReceivableFile(
        "", "", "APIBI", new BusinessObjectData(), returnMap);
    mockStatic.verify(
        () -> Util.getMetaDataByKey(any(BusinessObjectData.class), anyString()), times(2));
    verify(receivableJsonFileRepository).save(any());
    assertEquals(Constants.FATAL, returnMap.get(Constants.TYPE));
  }

  @Test
  public void testHandleUnRetryableException() {
    String expectedId = "testId";
    Map<String, Object> returnMap = new HashMap<>();
    Mockito.when(receivableJsonFileRepository.findByTransmissionId(anyString()))
        .thenReturn(Optional.of(new ReceivableJsonFileEntity()));
    processDmReceivableFileService.handleUnRetryableException(
        expectedId, "testUrl", "testRS", new BusinessObjectDataStatusChangeEvent(), returnMap);
    Mockito.verify(receivableJsonFileRepository).save(any());
    assertEquals(expectedId, returnMap.get(Constants.TRANSMISSION_ID_KEY).toString());
  }

  @Test
  public void testHandleUnRetryableException_beforeInsert() {
    String expectedId = "testId";
    Map<String, Object> returnMap = new HashMap<>();
    Mockito.when(receivableJsonFileRepository.findByTransmissionId(anyString()))
        .thenReturn(Optional.empty());
    processDmReceivableFileService.handleUnRetryableException(
        expectedId, "testUrl", "testRS", new BusinessObjectDataStatusChangeEvent(), returnMap);
    Mockito.verify(receivableJsonFileRepository).save(any());
    assertEquals(expectedId, returnMap.get(Constants.TRANSMISSION_ID_KEY).toString());
  }

  @Test
  public void testHandleUnRetryableException_whenFailBeforeTransmissionIDGeneration() {
    Map<String, Object> returnMap = new HashMap<>();
    Mockito.when(receivableJsonFileRepository.findByTransmissionId(anyString()))
        .thenReturn(Optional.empty());
    processDmReceivableFileService.handleUnRetryableException(null, null, null, null, returnMap);
    Mockito.verify(receivableJsonFileRepository).save(any());
    assertTrue(
        StringUtils.isNotBlank(returnMap.get(Constants.TRANSMISSION_ID_KEY).toString()));
  }
}
