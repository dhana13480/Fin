package org.finra.rmcs.function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.HashMap;
import java.util.Map;
import org.finra.herd.sdk.model.BusinessObjectData;
import org.finra.herd.sdk.model.BusinessObjectDataKey;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;
import org.finra.rmcs.service.impl.HerdServiceImpl;
import org.finra.rmcs.service.impl.ProcessDmReceivableFileServiceImpl;
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

@SpringJUnitConfig
public class ProcessDMReceivableFileTest {

  @InjectMocks
  private ProcessDMReceivableFile processDMReceivableFile;

  @Mock
  private ProcessDmReceivableFileServiceImpl processDmReceivableFileService;

  @Mock
  private HerdServiceImpl herdService;

  private MockedStatic<Util> mockStatic;

  @BeforeEach
  public void before() {
    mockStatic = Mockito.mockStatic(Util.class);
  }

  @AfterEach
  public void after() {
    mockStatic.close();
  }

  @Test
  public void testProcessDMReceivable_success() throws Exception {
    Map<String, Object> input = new HashMap<>();
    String dmEvent = TestUtil.getResourceFileContents("/ValidBizObjChangeEvent.json");
    input.put(Constants.BIZ_OBJ_STATUS_CHANGE_EVENT, dmEvent);
    input.put(Constants.SNS_MESSAGE_ID, "testId");
    Mockito.when(herdService.getBusinessObjectData(any(BusinessObjectDataKey.class)))
        .thenReturn(new BusinessObjectData());
    Mockito.when(Util.generateS3Location(any(BusinessObjectData.class))).thenReturn("testLocation");
    Mockito.when(Util.generateTransmissionId(any(BusinessObjectData.class)))
        .thenReturn("testTransmissionId");
    Mockito.when(Util.getRevenueStreamFromNameSpace(any(BusinessObjectDataStatusChangeEvent.class)))
        .thenReturn("testRevenueStream");
    Mockito.doNothing()
        .when(processDmReceivableFileService)
        .upsertEntry(
            anyString(),
            anyString(),
            anyString(),
            any(BusinessObjectDataStatusChangeEvent.class),
            anyMap());
    Mockito.doNothing()
        .when(processDmReceivableFileService)
        .validateReceivableFile(
            anyString(), anyString(), anyString(), any(BusinessObjectData.class), anyMap());
    processDMReceivableFile.apply(input);
    Mockito.verify(processDmReceivableFileService)
        .upsertEntry(
            anyString(),
            anyString(),
            anyString(),
            any(BusinessObjectDataStatusChangeEvent.class),
            anyMap());
    Mockito.verify(processDmReceivableFileService)
        .validateReceivableFile(
            anyString(), anyString(), anyString(), any(BusinessObjectData.class), anyMap());
  }

  @Test
  public void testProcessDMReceivable_error() throws Exception {
    Map<String, Object> input = new HashMap<>();
    String dmEvent = TestUtil.getResourceFileContents("/InvalidBizObjChangeEvent.json");
    input.put(Constants.BIZ_OBJ_STATUS_CHANGE_EVENT, dmEvent);
    input.put(Constants.SNS_MESSAGE_ID, "testId");
    Mockito.doNothing()
        .when(processDmReceivableFileService)
        .handleUnRetryableException(any(), any(), any(), any(), any());
    processDMReceivableFile.apply(input);
    Mockito.verify(processDmReceivableFileService)
        .handleUnRetryableException(any(), any(), any(), any(), any());
  }
}
