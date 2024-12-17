package org.finra.rmcs.util;

import static java.lang.System.getenv;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;

@Slf4j
public class Util {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static void populateReturnMap(
      String status, String details, Map<String, Object> returnMap) {
    returnMap.put(Constants.COMPLETION_STATUS, status);
    returnMap.put(Constants.COMPLETION_DETAILS, details);
  }

  public static boolean isMessageValid(SQSEvent sqsEvent) {
    SQSEvent.SQSMessage sqsRecord = sqsEvent.getRecords().get(0);
    String json = sqsRecord.getBody();
    JsonNode msgBodyNode;
    try {
      msgBodyNode = new ObjectMapper().readTree(json);
    } catch (JsonProcessingException e) {
      log.info("Not a valid json {}", json);
      return false;
    }
    return msgBodyNode.get(Constants.SQS_MESSAGE_ATTRIBUTES) != null;
  }

  @SneakyThrows
  public static String getMessageId(SQSEvent sqsEvent) {
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
      log.info("msgBodyNode: {}", msgBodyNode);
      return null;
    }
  }

  public static BusinessObjectDataStatusChangeEvent getBusinessObjectDataStatusChangeEvent(
      SQSEvent sqsEvent) throws Exception {
    SQSEvent.SQSMessage sqsRecord = sqsEvent.getRecords().get(0);
    JsonNode msgBodyNode = objectMapper.readTree(sqsRecord.getBody());
    String payload = msgBodyNode.get(Constants.SQS_EVENT_MESSAGE).asText();
    log.info("msgBodyNode: {}", msgBodyNode);
    log.info("payload: {}", payload);

    return objectMapper.readValue(payload, BusinessObjectDataStatusChangeEvent.class);
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
}
