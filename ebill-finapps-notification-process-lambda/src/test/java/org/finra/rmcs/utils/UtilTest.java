package org.finra.rmcs.utils;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class UtilTest {

  @BeforeEach
  public void setup() {
  }

  @Test
  public void isDryRunTrueTest() {
    String testQueueMessage = "{\"Message\" : \"{ \\\"dryRun\\\" }\" }";
    SQSEvent sqsEvent = Util.getSqsEvent(testQueueMessage);
    Assertions.assertTrue(Util.isDryRun(sqsEvent));
  }

  @Test
  public void isDryRunFalseTest() {
    String testQueueMessage = "{\"Message\" : \"{ \\\"dryRunFalse\\\" }\" }";
    SQSEvent sqsEvent = Util.getSqsEvent(testQueueMessage);
    Assertions.assertFalse(Util.isDryRun(sqsEvent));
  }

  @Test
  public void isHealthCheckTrueTest() {
    String testQueueMessage = "{\"Message\" : \"{ \\\"health\\\" }\" }";
    SQSEvent sqsEvent = Util.getSqsEvent(testQueueMessage);
    Assertions.assertTrue(Util.isHealthCheck(sqsEvent));
  }

  @Test
  public void isHealthCheckFalseTest() {
    String testQueueMessage = "{\"Message\" : \"{ \\\"healthFalse\\\" }\" }";
    SQSEvent sqsEvent = Util.getSqsEvent(testQueueMessage);
    Assertions.assertFalse(Util.isHealthCheck(sqsEvent));
  }

  @Test
  public void getSqsEventTest() {
    String testQueueMessage = "{\"Message\" : \"{ \\\"health\\\" }\" }";
    SQSEvent actual = Util.getSqsEvent(testQueueMessage);
    Assertions.assertEquals(testQueueMessage, actual.getRecords().get(0).getBody().toString());
  }

}
