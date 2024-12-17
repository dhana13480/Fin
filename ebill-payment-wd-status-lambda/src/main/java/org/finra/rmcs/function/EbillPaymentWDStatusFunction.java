package org.finra.rmcs.function;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.model.NotificationMessage;
import org.finra.rmcs.service.impl.PaymentTrackingWDStatusServiceImpl;
import org.finra.rmcs.utils.Util;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EbillPaymentWDStatusFunction implements
    Function<SQSEvent, Map<String, Object>> {

  private final PaymentTrackingWDStatusServiceImpl paymentTrackingWDStatusService;

  @Override
  public Map<String, Object> apply(SQSEvent sqsEvent) {
    UUID uuid = UUID.randomUUID();
    String correlationId = uuid.toString();
    String methodName =
        Constants.CLASS
            + this.getClass().getSimpleName()
            + Constants.SPACE
            + Constants.METHOD
            + Thread.currentThread().getStackTrace()[1].getMethodName()
            + Constants.SPACE
            + Constants.CORRELATION_ID
            + correlationId;
    log.info("{} message: method entry", methodName);
    Map<String, Object> returnMap = new HashMap<>();

    if (Util.isDryRun(sqsEvent)) {
      log.info("{} message: dry run mode", methodName);
      returnMap.put(Constants.DRY_RUN, "success");
      return returnMap;
    }

    String messageId = Util.getMessageId(sqsEvent, correlationId);

    if (StringUtils.isBlank(messageId)) {
      log.info("{} SNS Message ID not found in the SQS body", methodName);
      Util.populateReturnMap(
          Constants.COMPLETION_STATUS_SKIP,
          Constants.COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_MESSAGE_ID_NOT_FOUND,
          returnMap);
      log.info(Constants.RETURN_MAP_VALUE_LOG, returnMap);
      return returnMap;
    }

    try {
      NotificationMessage notificationMessage = Util.getNotificationMessage(sqsEvent);
      log.info("message received from sqs: {}", notificationMessage);
      paymentTrackingWDStatusService.updatePaymentTrackingWDStatus(messageId, notificationMessage);
    } catch (Exception ex) {
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
