package org.finra.rmcs.util;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.constants.Constants;

@Slf4j
public class TestUtil {

  public static String EVENT_SOURCE =
      "arn:aws:sns:us-east-1:465257512377:ESMP-X-D-RMCS-RECEIVABLE-NOTIFICATION_DEV02_QUEUE";

  public static String getResourceFileContents(String fileName) throws Exception {
    try {
      InputStream is = TestUtil.class.getResourceAsStream(fileName);
      InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
      BufferedReader reader = new BufferedReader(streamReader);
      StringBuilder buffer = new StringBuilder();
      for (String line; (line = reader.readLine()) != null; ) {
        buffer.append(line).append("\n");
      }
      return buffer.toString();
    } catch (Exception e) {
      log.error(String.format(" ### Error Reading Test File: %s ###", fileName), e);
      throw e;
    }
  }

  public static SQSEvent getSQSEvent(String resourceFileName) throws Exception {
    SQSEvent sqsEvent = new SQSEvent();
    SQSMessage sqsMessage = new SQSMessage();
    String resourceFileContents = getResourceFileContents(resourceFileName);
    sqsMessage.setBody(resourceFileContents);
    ObjectMapper mapper = new ObjectMapper();
    if (resourceFileName.equalsIgnoreCase("/ValidDMEvent.json")) {
      JsonNode eventSource = mapper.valueToTree("aws:sqs");
      JsonNode eventSourceArn =
          mapper.valueToTree(
              "arn:aws:sqs:us-east-1:465257512377:ESMP-X-D-RMCS-RECEIVABLE-PAYMENT_NOTIFICATION_DEV02_QUEUE");
      sqsMessage.setEventSource("aws:sqs");
      sqsMessage.setEventSourceArn(
          "arn:aws:sqs:us-east-1:465257512377:ESMP-X-D-RMCS-RECEIVABLE-PAYMENT_NOTIFICATION_DEV02_QUEUE");
    } else {
      JsonNode eventSource = mapper.valueToTree("aws:sns");
      JsonNode eventSourceArn =
          mapper.valueToTree("arn:aws:sqs:us-east-1:465257512377:ESMP-X-D-RMCS");
      sqsMessage.setEventSource(String.valueOf(eventSource));
      sqsMessage.setEventSourceArn(String.valueOf(eventSourceArn));
    }
    List<SQSMessage> recordsList = new ArrayList<>();
    recordsList.add(sqsMessage);
    sqsEvent.setRecords(recordsList);
    return sqsEvent;
  }

  public static SQSEvent getSQSEvent(String resourceFileName, String eventSourceArn)
      throws Exception {
    SQSEvent sqsEvent = new SQSEvent();
    SQSMessage sqsMessage = new SQSMessage();
    String resourceFileContents = getResourceFileContents(resourceFileName);
    sqsMessage.setBody(resourceFileContents);
    sqsMessage.setEventSource(Constants.SQS_EVENT_SOURCE);
    sqsMessage.setEventSourceArn(eventSourceArn);
    List<SQSMessage> recordsList = new ArrayList<>();
    recordsList.add(sqsMessage);
    sqsEvent.setRecords(recordsList);
    return sqsEvent;
  }
}
