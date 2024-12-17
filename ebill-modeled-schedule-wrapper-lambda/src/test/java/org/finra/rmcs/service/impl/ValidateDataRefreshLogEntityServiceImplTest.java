package org.finra.rmcs.service.impl;

import static org.mockito.Mockito.when;

import org.finra.rmcs.entity.DataRefreshLogEntity;
import org.finra.rmcs.repo.DataRefreshLogRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class ValidateDataRefreshLogEntityServiceImplTest {

  @InjectMocks
  ValidateDataRefreshLogServiceImpl validateDataRefreshLogService;

  @Mock
  DataRefreshLogRepo dataRefreshLogRepo;

  @Mock
  private NotificationServiceImpl notificationService;

  @Test
  public void test_execute() {
    DataRefreshLogEntity dataRefreshLogEntity = new DataRefreshLogEntity();
    dataRefreshLogEntity.setStatus("Completed");
    when(dataRefreshLogRepo.fetchDataRefreshLog()).thenReturn(dataRefreshLogEntity);
    String res = validateDataRefreshLogService.execute("test123");
    Assertions.assertEquals("SUCCESS", res);
  }

  @Test
  public void test_execute_Failure() {
    DataRefreshLogEntity dataRefreshLogEntity = new DataRefreshLogEntity();
    dataRefreshLogEntity.setStatus("Test");
    when(dataRefreshLogRepo.fetchDataRefreshLog()).thenReturn(dataRefreshLogEntity);
    Assertions.assertThrows(RuntimeException.class, () -> validateDataRefreshLogService.execute("test123"));
  }
}