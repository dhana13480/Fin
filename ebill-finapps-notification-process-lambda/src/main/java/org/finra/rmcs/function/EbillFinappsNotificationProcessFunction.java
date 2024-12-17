package org.finra.rmcs.function;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.dto.DmNotification;
import org.finra.rmcs.service.InvoiceFileSummaryService;
import org.finra.rmcs.utils.Util;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EbillFinappsNotificationProcessFunction implements
    Function<SQSEvent, Map<String, Object>> {

  private final InvoiceFileSummaryService invoiceFileSummaryService;
  @Override
  public Map<String, Object> apply(SQSEvent sqsEvent) {
    UUID uuid = UUID.randomUUID();
    String correlationId = uuid.toString();
    String methodName =
        new StringBuilder()
            .append(Constants.CLASS)
            .append(this.getClass().getSimpleName())
            .append(Constants.SPACE)
            .append(Constants.METHOD)
            .append(Thread.currentThread().getStackTrace()[1].getMethodName())
            .append(Constants.SPACE)
            .append(Constants.CORRELATION_ID)
            .append(correlationId)
            .toString();
    log.info("{} message: method entry", methodName);
    log.info("sqsEvent:{}",sqsEvent);
    Map<String, Object> returnMap = new HashMap<>();
    if (Util.isDryRun(sqsEvent)) {
      log.info("{} message: dry run mode", methodName);
      returnMap.put(Constants.DRY_RUN, "success");
      return returnMap;
    }

    if (Util.isHealthCheck(sqsEvent)) {
      log.info("{} message: dry run mode", methodName);
      returnMap.put(Constants.HEALTH_PAYLOAD, "success");
      return returnMap;
    }

    try{
      DmNotification notificationMessage = Util.getNotificationMessage(sqsEvent);
      log.info("message received from sqs: {}", notificationMessage);
      //save the dm message into invoice file summary and invoice file detail entity
      invoiceFileSummaryService.storeDmNotificationIntoInvoiceFileSummary(correlationId, notificationMessage);
      log.info("notification processed successfully for correlationId: {}", correlationId);
    }catch(Exception ex){
      log.error("{} Exception occurred {}", methodName, ex);
      Util.populateReturnMap(
          Constants.COMPLETION_STATUS_FAILED,
          Constants.COMPLETION_FAILED_DETAILS,
          returnMap);
      return returnMap;
    }

    log.info(Constants.RETURN_MAP_VALUE_LOG, returnMap);
    Util.populateReturnMap(
        Constants.COMPLETION_STATUS_SUCCESS,
        Constants.COMPLETION_SUCCESS_DETAILS,
        returnMap);
    return returnMap;
  }
}
