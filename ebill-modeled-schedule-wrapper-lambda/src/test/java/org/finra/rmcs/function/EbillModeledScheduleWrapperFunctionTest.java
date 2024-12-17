package org.finra.rmcs.function;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.finra.rmcs.TestFileReaderUtil;
import org.finra.rmcs.entity.DataRefreshLogEntity;
import org.finra.rmcs.repo.DataRefreshLogRepo;
import org.finra.rmcs.service.EbillModeledScheduleWrapperServiceLocator;
import org.finra.rmcs.service.impl.NotificationServiceImpl;
import org.finra.rmcs.service.impl.ValidateDataRefreshLogServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class EbillModeledScheduleWrapperFunctionTest {

  static Map<String, Object> dryRunRequest;
  static Map<String, Object> dataRefreshLogRequest;

  @InjectMocks
  EbillModeledScheduleWrapperFunction ebillModeledScheduleWrapperFunction;

  @Mock
  EbillModeledScheduleWrapperServiceLocator serviceLocator;

  @Mock
  DataRefreshLogRepo dataRefreshLogRepo;

  @Mock
  private NotificationServiceImpl notificationService;

  @BeforeEach
  public void init() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    dryRunRequest = mapper.readerFor(Object.class)
        .readValue(TestFileReaderUtil.getResourceContent(
            "EbillModeledScheduleWrapperDryRunRequest.json"));

    dataRefreshLogRequest = mapper.readerFor(Object.class)
        .readValue(TestFileReaderUtil.getResourceContent(
            "EbillModeledScheduleWrapperRequest.json"));

  }

  @Test
  void apply_Dryrun() {
    String response = ebillModeledScheduleWrapperFunction.apply(dryRunRequest);
    Assertions.assertEquals("success", response);
  }

  @Test
  public void apply_Success() {
    DataRefreshLogEntity dataRefreshLogEntity = new DataRefreshLogEntity();
    dataRefreshLogEntity.setStatus("Completed");
    when(dataRefreshLogRepo.fetchDataRefreshLog()).thenReturn(dataRefreshLogEntity);
    Mockito.when(serviceLocator.locateService(Mockito.any())).thenReturn( new ValidateDataRefreshLogServiceImpl(dataRefreshLogRepo, notificationService));
    String response = ebillModeledScheduleWrapperFunction.apply(dataRefreshLogRequest);
    Assertions.assertEquals("SUCCESS", response);
  }

  @Test
  public void apply_Exception() {
    DataRefreshLogEntity dataRefreshLogEntity = new DataRefreshLogEntity();
    dataRefreshLogEntity.setStatus("Test");
    when(dataRefreshLogRepo.fetchDataRefreshLog()).thenReturn(dataRefreshLogEntity);
    Mockito.when(serviceLocator.locateService(Mockito.any())).thenReturn( new ValidateDataRefreshLogServiceImpl(dataRefreshLogRepo, notificationService));
    Assertions.assertThrows(RuntimeException.class, () -> ebillModeledScheduleWrapperFunction.apply(dataRefreshLogRequest));
  }
}