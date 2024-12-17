package org.finra.rmcs.function;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.service.EbillModeledScheduleWrapperService;
import org.finra.rmcs.service.EbillModeledScheduleWrapperServiceLocator;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class EbillModeledScheduleWrapperFunction implements
    Function<Map<String, Object>, String>{

  private EbillModeledScheduleWrapperServiceLocator serviceLocator;

  @SneakyThrows
  @Override
  public String apply(Map<String, Object> requestMap) {
    String correlationId = UUID.randomUUID().toString();
    String status = null;
    String methodName =
        new StringBuilder()
            .append(Constants.CLASS)
            .append(this.getClass().getSimpleName())
            .append(" ")
            .append(Constants.METHOD)
            .append(Thread.currentThread().getStackTrace()[1].getMethodName())
            .append(" ")
            .append(Constants.CORRELATION_ID)
            .append(correlationId)
            .append(" ")
            .append(Constants.INPUT)
            .append(requestMap)
            .toString();
    log.info("{} message: method entry", methodName);

    boolean dryRun =
        Boolean.parseBoolean(requestMap.getOrDefault(Constants.DRY_RUN, Constants.FALSE).toString());
    log.info("dryRun: {}", dryRun);
    if (dryRun) {
      log.info("dry run mode");
      return "success";
    }
    log.info("Start {} Ebill Modeled Schedule Wrapper with correlationId: {}.", requestMap.get(Constants.SERVICE_NAME).toString(), correlationId);
    EbillModeledScheduleWrapperService ebillModeledScheduleWrapperService = this.serviceLocator.locateService(requestMap.get(Constants.SERVICE_NAME).toString());
    if(ebillModeledScheduleWrapperService != null){
      status = ebillModeledScheduleWrapperService.execute(correlationId);
    }
    log.info("End of {} Ebill Modeled Schedule Wrapper with status: {} for correlationId: {}", requestMap.get(Constants.SERVICE_NAME).toString(), status, correlationId);
    return status;
  }

}
