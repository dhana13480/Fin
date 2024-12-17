package org.finra.rmcs.util;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.model.PaymentNoticeChangeEvent;

@Slf4j
public class Util {

  public static final String methodName =
      new StringBuilder()
          .append(Constants.CLASS)
          .append("Util")
          .append(Constants.SPACE)
          .append(Constants.METHOD)
          .append(Thread.currentThread().getStackTrace()[1].getMethodName())
          .append(Constants.SPACE)
          .toString();
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static void populateReturnMap(
      String status, String details, Map<String, Object> returnMap) {
    returnMap.put(Constants.COMPLETION_STATUS, status);
    returnMap.put(Constants.COMPLETION_DETAILS, details);
  }

  @SneakyThrows
  public static String getMessageId(SQSEvent sqsEvent, String correlationId) {
    SQSEvent.SQSMessage sqsRecord = sqsEvent.getRecords().get(0);
    JsonNode msgBodyNode = objectMapper.readTree(sqsRecord.getBody());
    String type =
        msgBodyNode.get(Constants.SQS_TYPE) != null
            ? msgBodyNode.get(Constants.SQS_TYPE).asText()
            : null;
    String messageId =
        msgBodyNode.get(Constants.SNS_MESSAGE_ID) != null
            ? msgBodyNode.get(Constants.SNS_MESSAGE_ID).asText()
            : null;
    if (Constants.SQS_NOTIFICATION.equals(type) && !StringUtils.isBlank(messageId)) {
      return messageId;
    } else {
      log.info(
          "{} message : Invalid SQS Type or Message Id is not present",
          new StringBuilder()
              .append(methodName)
              .append(Constants.SPACE)
              .append(Constants.CORRELATION_ID)
              .append(correlationId)
              .toString());
      return null;
    }
  }

  public static PaymentNoticeChangeEvent getPaymentNoticeChangeEvent(SQSEvent sqsEvent)
      throws Exception {
    SQSEvent.SQSMessage sqsRecord = sqsEvent.getRecords().get(0);
    JsonNode msgBodyNode = objectMapper.readTree(sqsRecord.getBody());
    String payload = msgBodyNode.get(Constants.SQS_EVENT_MESSAGE).asText();
    return objectMapper.readValue(payload, PaymentNoticeChangeEvent.class);
  }

  @SneakyThrows
  public static boolean isSourceValid(SQSEvent sqsEvent) {
    SQSEvent.SQSMessage sqsRecord = sqsEvent.getRecords().get(0);
    String eventSource = sqsRecord.getEventSource();
    String eventSourceARN = sqsRecord.getEventSourceArn();
    String predefinedEventSourceARN = System.getenv(Constants.EVENT_SOURCE);
    return StringUtils.isNotBlank(eventSource)
        && eventSource.equalsIgnoreCase(Constants.SQS_EVENT_SOURCE)
        && StringUtils.isNotBlank(eventSourceARN)
        && eventSourceARN.equalsIgnoreCase(predefinedEventSourceARN);
  }

  @SneakyThrows
  public static boolean isDryRun(SQSEvent sqsEvent) {
    boolean isDryRun = false;
    SQSEvent.SQSMessage sqsRecord = sqsEvent.getRecords().get(0);
    JsonNode msgBodyNode = objectMapper.readTree(sqsRecord.getBody());
    JsonNode messageAttributesNode = msgBodyNode.get(Constants.SQS_MESSAGE_ATTRIBUTES);
    JsonNode dryRunNode =
        messageAttributesNode != null ? messageAttributesNode.get(Constants.DRY_RUN) : null;
    if (dryRunNode != null) {
      String valueText = dryRunNode.get("Value").asText();
      isDryRun = Boolean.parseBoolean(valueText);
    }
    return isDryRun;
  }
}