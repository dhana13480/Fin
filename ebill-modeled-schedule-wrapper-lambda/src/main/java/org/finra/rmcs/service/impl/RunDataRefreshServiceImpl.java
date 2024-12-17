package org.finra.rmcs.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.AlertStatusEnum;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.constants.ModuleEnum;
import org.finra.rmcs.dto.EbillRunDataRefreshResponse;
import org.finra.rmcs.entity.DataRefreshLogEntity;
import org.finra.rmcs.entity.EbillBatchSchedulerEntity;
import org.finra.rmcs.exception.TokenException;
import org.finra.rmcs.repo.ConnectReplicationStatusRepo;
import org.finra.rmcs.repo.DataRefreshLogRepo;
import org.finra.rmcs.repo.EbillBatchSchedulerRepo;
import org.finra.rmcs.service.EbillModeledScheduleWrapperService;
import org.finra.rmcs.service.EwsTokenservice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service(Constants.RUN_DATA_REFRESH)
@RequiredArgsConstructor
public class RunDataRefreshServiceImpl implements EbillModeledScheduleWrapperService {

  private final DataRefreshLogRepo dataRefreshLogRepo;
  private final ConnectReplicationStatusRepo connectReplicationStatusRepo;
  private final EbillBatchSchedulerRepo ebillBatchSchedulerRepo;
  private final NotificationServiceImpl notificationService;
  private final RestTemplate restTemplate;
  private final EwsTokenservice ewsTokenService;

  @Value("${ebill.fargate.batch.runDataRefresh.url}")
  private String runDataRefreshUrl;

  @Value("${ebill.fargate.batch.runDataRefreshAlert1}")
  private String runDataRefreshAlert1;

  @Value("${ebill.fargate.batch.runDataRefreshAlert2}")
  private String runDataRefreshAlert2;

  @Override
  public String execute(String correlationId) {
    String methodName = Constants.CLASS + this.getClass().getSimpleName() + " " + Constants.METHOD
        + Thread.currentThread().getStackTrace()[1].getMethodName();
    log.info("{} Start Run Data Refresh process", methodName);
    String alert1[] = runDataRefreshAlert1.split("-");
    String alert2[] = runDataRefreshAlert2.split("-");
    LocalDateTime todayAlert1 = LocalDate.now().atTime(Integer.parseInt(alert1[0]), Integer.parseInt(alert1[1]));
    LocalDateTime todayAlert2 = LocalDate.now().atTime(Integer.parseInt(alert2[0]), Integer.parseInt(alert2[1]));
    try {
      List<String> status = connectReplicationStatusRepo.fetchStatusFromConnectReplicationStatus();
      if (CollectionUtils.isEmpty(status) || status.stream().anyMatch(e -> !e.equalsIgnoreCase(Constants.VALID))) {
        if (LocalDateTime.now().compareTo(todayAlert1) == 1) {
          log.info("Crisp Data Invalid and reached 04:30AM and triggering notification");
          notificationService.publishNotificationEvent(
              correlationId,
              ModuleEnum.ENABLER.name(),
              AlertStatusEnum.RUN_DATA_REFRESH_TIME_FOUR_THIRTY_EXCEED.name());
        }
      } else {
        DataRefreshLogEntity dataRefreshLogEntity = dataRefreshLogRepo.fetchDataRefreshLog();
        if (dataRefreshLogEntity == null) {
          EbillBatchSchedulerEntity ebillBatchSchedulerEntity = ebillBatchSchedulerRepo.fetchEbillBatchScheduler();
          if (ebillBatchSchedulerEntity != null) {
            if (Constants.FAILED.equalsIgnoreCase(ebillBatchSchedulerEntity.getStatus())) {
              log.info("Ebill Batch Scheduler Failed Status");
              throw new RuntimeException(
                  String.format("Ebill Batch Scheduler Failed Status!"));
            } else if (LocalDateTime.now().compareTo(todayAlert2) == 1) {
              log.info("Processing reached 07:00AM and triggering notification");
              notificationService.publishNotificationEvent(
                  correlationId,
                  ModuleEnum.ENABLER.name(),
                  AlertStatusEnum.RUN_DATA_REFRESH_TIME_SEVEN_EXCEED.name());
            }
          } else {
            ResponseEntity<EbillRunDataRefreshResponse> res = callRunDataRefresh();
            if (res.getStatusCode().equals(HttpStatus.OK)) {
              log.info("Stored procedure completed");
              return Constants.SUCCESS;
            } else {
              log.info("Stored procedure Failed");
              return Constants.FAILED;
            }
          }
        } else if (!StringUtils.equalsIgnoreCase(Constants.COMPLETED, dataRefreshLogEntity.getStatus())) {
          log.error("Data Refresh Log Entity status Failed");
          throw new RuntimeException(
              String.format("Data Refresh Log Entity status Failed!"));
        } else {
          notificationService.publishNotificationEvent(
              correlationId,
              ModuleEnum.ENABLER.name(),
              AlertStatusEnum.RUN_DATA_REFRESH_SUCCESS.name());
          log.info("Run Data Refresh Success");
        }
      }
    } catch(Exception ex) {
      log.error("Exception during Run Data Refresh :{}", ex);
      throw new RuntimeException(
          String.format("Run Data Refresh failed!"));
    }
    log.info(" Successfully End Of Run Data Refresh process");
    return Constants.SUCCESS;
  }

  @Async
  public ResponseEntity<EbillRunDataRefreshResponse> callRunDataRefresh()
      throws TokenException {
    log.info("Start Completable Future callRunDataRefresh");
    HttpEntity<Map<String, String>> httpEntity =
        new HttpEntity<Map<String, String>>(buildHeader());
    String url = buildUrl(runDataRefreshUrl);
    return restTemplate.exchange(url, HttpMethod.POST, httpEntity, EbillRunDataRefreshResponse.class);
  }

  private String buildUrl(String runDataRefreshUrl) {
    return runDataRefreshUrl + Constants.SELECTED_ORG_ID + "123";
  }

  private HttpHeaders buildHeader() throws TokenException {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(ewsTokenService.getEwsToken());
    return headers;
  }
}
