package org.finra.rmcs.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.constants.AlertStatusEnum;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.constants.ModuleEnum;
import org.finra.rmcs.entity.DataRefreshLogEntity;
import org.finra.rmcs.repo.DataRefreshLogRepo;
import org.finra.rmcs.service.EbillModeledScheduleWrapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(Constants.DATA_REFRESH_LOG)
@AllArgsConstructor
public class ValidateDataRefreshLogServiceImpl implements EbillModeledScheduleWrapperService {

  private DataRefreshLogRepo dataRefreshLogRepo;

  @Autowired
  private NotificationServiceImpl notificationService;

  @Override
  public String execute(String correlationId) {
    String methodName = Constants.CLASS + this.getClass().getSimpleName() + " " + Constants.METHOD
        + Thread.currentThread().getStackTrace()[1].getMethodName();
    log.info("{} Start Validate Data Refresh Log process", methodName);
    try {
      validateDataRefreshLog(correlationId);
    } catch(Exception ex) {
      log.error("Exception during Validate Data Refresh Log :{}", ex);
      throw new RuntimeException(
          String.format("Validate Data Refresh Log not completed!"));
    }
    log.info(" Successfully End Of Validate Data Refresh Log");
    return Constants.SUCCESS;
  }

  public void validateDataRefreshLog(String correlationId) {
    DataRefreshLogEntity dataRefreshLogEntity = dataRefreshLogRepo.fetchDataRefreshLog();
    if (dataRefreshLogEntity != null && Constants.COMPLETED.equalsIgnoreCase(dataRefreshLogEntity.getStatus())) {
      notificationService.publishNotificationEvent(
          correlationId,
          ModuleEnum.ENABLER.name(),
          AlertStatusEnum.DATA_REFRESH_LOG_SUCCESS.name());
      log.info("Validate Data Refresh Log ");
    } else {
      log.info("Validate Data Refresh Log not completed");
      throw new RuntimeException(
          String.format("Validate Data Refresh Log not completed!"));
    }
  }
}
