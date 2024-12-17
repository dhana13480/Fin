package org.finra.rmcs.function;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.exception.ReceivablePaymentNotificationException;
import org.finra.rmcs.model.PaymentNoticeChangeEvent;
import org.finra.rmcs.service.impl.ReceivablePaymentNotificationServiceImpl;
import org.finra.rmcs.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReceivablePaymentNotificationLambda
    implements Function<SQSEvent, Map<String, Object>> {

  private final ReceivablePaymentNotificationServiceImpl receivablePaymentNotificationService;

  @Autowired
  public ReceivablePaymentNotificationLambda(
      ReceivablePaymentNotificationServiceImpl receivablePaymentNotificationService) {
    this.receivablePaymentNotificationService = receivablePaymentNotificationService;
  }

  /**
   * Applies this function to the given argument.
   *
   * @param sqsEvent the function argument
   * @return the function result
   */
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
    Map<String, Object> returnMap = new HashMap<>();

    if (Util.isDryRun(sqsEvent)) {
      log.info("{} message: dry run mode", methodName);
      returnMap.put(Constants.DRY_RUN, "success");
      return returnMap;
    }

    if (!Util.isSourceValid(sqsEvent)) {
      log.info("{} message: Invalid Message source", methodName);
      Util.populateReturnMap(
          Constants.COMPLETION_STATUS_SKIP,
          Constants.COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_SOURCE,
          returnMap);
      log.info(Constants.RETURN_MAP_VALUE_LOG, returnMap);
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
      PaymentNoticeChangeEvent paymentNoticeChangeEvent =
          Util.getPaymentNoticeChangeEvent(sqsEvent);

      methodName =
          new StringBuilder()
              .append(methodName)
              .append(Constants.SPACE)
              .append(Constants.SNS_MESSAGEID)
              .append(messageId)
              .append(Constants.SPACE)
              .append(Constants.TRANSMISSION_ID)
              .append(paymentNoticeChangeEvent.getTransmissionId())
              .toString();
      log.info("{} message: captured payment information", methodName);

      if (Constants.PAYMENT_STATUS_PAID.equalsIgnoreCase(paymentNoticeChangeEvent.getEventName())) {
        log.info(
            "{} message: Received a PAID Payment Notification Event for invoice_id: {}",
            methodName,
            paymentNoticeChangeEvent.getInvoiceId());

        receivablePaymentNotificationService.updateReceivablePaymentStatus(
            correlationId, paymentNoticeChangeEvent);

        log.info("{} message: Updated Successfully", methodName);

        Util.populateReturnMap(
            Constants.COMPLETION_STATUS_SUCCESS,
            Constants.COMPLETION_DETAILS_VALID_DM_MESSAGE,
            returnMap);
      } else {
        log.info(
            "{} message: Received a {} Payment Notification Event for invoice_id: {}, skipping",
            methodName,
            paymentNoticeChangeEvent.getEventName(),
            paymentNoticeChangeEvent.getInvoiceId());
      }
    } catch (Exception e) {
      log.error("{} Exception occurred {}", methodName, e.getMessage());
      // throw new Exception for the time being, it may be enhanced in the future with an email
      // notification.
      throw new ReceivablePaymentNotificationException(e.getMessage());
    }
    log.info(Constants.RETURN_MAP_VALUE_LOG, returnMap);
    return returnMap;
  }
}
