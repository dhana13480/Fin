package org.finra.rmcs.function;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.constants.ModuleTypeEnum;
import org.finra.rmcs.constants.StatusEnum;
import org.finra.rmcs.dto.NotificationMessage;
import org.finra.rmcs.service.AccountingEquationReportService;
import org.finra.rmcs.utils.Util;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class EbillAccountingEquationReportFunction
    implements Function<SQSEvent, Map<String, Object>> {
  private final AccountingEquationReportService accountingEquationReportService;

  @Override
  public Map<String, Object> apply(SQSEvent sqsEvent) {

    String correlationId = UUID.randomUUID().toString();
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
      log.info(
          "{}=> message received from sqs: {}",
          Constants.ACCOUNTING_EQUATION_REPORT,
          notificationMessage);
      if (notificationMessage != null
          && (notificationMessage.getModule() != null
              && notificationMessage.getModule().equalsIgnoreCase(ModuleTypeEnum.ENABLER.name()))
          && (notificationMessage.getStatus() != null
              && notificationMessage
                  .getStatus()
                  .equalsIgnoreCase(StatusEnum.RUN_DATA_REFRESH_SUCCESS.name()))) {

        boolean isEmailsend =
            accountingEquationReportService.sendAccountingEquationReportAsEmail(correlationId);
        if (isEmailsend) {
          log.info(
              Constants.LOG_FORMAT_WITH_EVENT,
              Constants.LAMBDA_NAME,
              methodName,
              correlationId,
              Constants.ACCOUNTING_EQUATION_REPORT_EMAIL_SENT_SUCCESS,
              String.format(
                  "with correlationId %s ,Successfully Accounting Equation Report Email Sent",
                  correlationId));

        } else {
          log.info(
              Constants.LOG_FORMAT_WITH_EVENT,
              Constants.LAMBDA_NAME,
              methodName,
              correlationId,
              Constants.ACCOUNTING_EQUATION_REPORT_EMAIL_SENT_FAILED,
              String.format(
                  "with correlationId %s , Exception during Send Email  Accounting Equation Report",
                  correlationId));
        }
      }

    } catch (Exception ex) {
      log.error(
          Constants.LOG_FORMAT_WITH_EVENT,
          Constants.LAMBDA_NAME,
          methodName,
          correlationId,
          Constants.ACCOUNTING_EQUATION_REPORT_EXCEPTION,
          String.format("%s Exception occurred %s", methodName, ex));
      throw new RuntimeException(
          String.format("For CorrelationId: %s Exception occurred: %s", correlationId, ex));
    }

    log.info(Constants.RETURN_MAP_VALUE_LOG, returnMap);
    Util.populateReturnMap(
        Constants.COMPLETION_STATUS_SUCCESS, Constants.COMPLETION_SUCCESS_DETAILS, returnMap);
    return returnMap;
  }
}
