package org.finra.rmcs.utils;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.model.NotificationMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Util {


  public static final String METHOD_NAME =
      Constants.CLASS
          + "Util"
          + Constants.SPACE
          + Constants.METHOD
          + Thread.currentThread().getStackTrace()[1].getMethodName()
          + Constants.SPACE;
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final Map<String, String> apiUserBean;

  public Util(Map<String, String> apiUserBean) {
    this.apiUserBean = apiUserBean;
  }

  private static String generateErrorMsg(String body, List<String> errMsgs) {
    if (errMsgs != null && errMsgs.size() > 0) {
      List<String> errMsg = new ArrayList<>();
      errMsgs.forEach(msg -> {
        errMsg.add(String.format("%s", msg));
      });
      return String.join("", errMsg);
    }
    return "";
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
              .append(METHOD_NAME)
              .append(Constants.SPACE)
              .append(Constants.CORRELATION_ID)
              .append(correlationId));
      return null;
    }
  }

  public static NotificationMessage getNotificationMessage(SQSEvent sqsEvent)
      throws JsonProcessingException {
    SQSEvent.SQSMessage sqsRecord = sqsEvent.getRecords().get(0);
    JsonNode msgBodyNode = objectMapper.readTree(sqsRecord.getBody());
    String payload = msgBodyNode.get(Constants.SQS_EVENT_MESSAGE).asText();
    return objectMapper.readValue(payload, NotificationMessage.class);
  }

}
