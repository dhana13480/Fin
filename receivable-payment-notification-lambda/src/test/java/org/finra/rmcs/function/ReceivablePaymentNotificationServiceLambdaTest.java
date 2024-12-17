package org.finra.rmcs.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.exception.ReceivablePaymentNotificationException;
import org.finra.rmcs.service.impl.ReceivablePaymentNotificationServiceImpl;
import org.finra.rmcs.util.TestUtil;
import org.finra.rmcs.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
public class ReceivablePaymentNotificationServiceLambdaTest {

  @Mock
  private ReceivablePaymentNotificationServiceImpl receivablePaymentNotificationService;
  @InjectMocks
  private ReceivablePaymentNotificationLambda receivablePaymentNotificationLambda;

  @Test
  public void testProcessSQSEvent_whenNonMessageAttributes_skip() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/NonSNSMessage.json");
    sqsEvent.getRecords().get(0).setMessageId("test");
    Map<String, Object> response = receivablePaymentNotificationLambda.apply(sqsEvent);
  }

  @Test
  public void testProcessSQSEvent_whenNonMessageId_skip() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/MissingMessageId.json");
    sqsEvent.getRecords().get(0).setMessageId("test");
    JsonNode eventSource = new ObjectMapper().valueToTree("aws:sqs");
    JsonNode eventSourceArn = new ObjectMapper().valueToTree(
        "arn:aws:sqs:us-east-1:465257512377:ESMP-X-D-RMCS-RECEIVABLE-DM_MESSAGE_QUEUE");
    sqsEvent.getRecords().get(0).setEventSourceArn(String.valueOf(eventSourceArn));
    sqsEvent.getRecords().get(0).setEventSource(String.valueOf(eventSource));
    try (MockedStatic<Util> mockCommon = mockStatic(Util.class, InvocationOnMock::callRealMethod)) {
      when(Util.isDryRun(sqsEvent)).thenReturn(false);
      when(Util.isSourceValid(sqsEvent)).thenReturn(true);
      Map<String, Object> response = receivablePaymentNotificationLambda.apply(sqsEvent);
      assertEquals(Constants.COMPLETION_STATUS_SKIP, response.get(Constants.COMPLETION_STATUS));
      assertEquals(
          Constants.COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_MESSAGE_ID_NOT_FOUND,
          response.get(Constants.COMPLETION_DETAILS));
    }
  }

  @Test
  public void testProcessSQSEvent_whenInvalidMessage_skip() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/InvalidDMEvent.json");
    sqsEvent.getRecords().get(0).setMessageId("test");
    try (MockedStatic<Util> mockCommon = mockStatic(Util.class, InvocationOnMock::callRealMethod)) {
      when(Util.isSourceValid(sqsEvent)).thenReturn(false);
      Map<String, Object> response = receivablePaymentNotificationLambda.apply(sqsEvent);
      assertEquals(
          Constants.COMPLETION_STATUS_SKIP,
          response.get(Constants.COMPLETION_STATUS), "Unexpected Completion Status!");
      assertEquals(
          Constants.COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_SOURCE,
          response.get(Constants.COMPLETION_DETAILS), "Unexpected Completion Details!");
    }
  }

  @Test
  public void testProcessSQSEvent_whenValidMessage() throws Exception {
    try (MockedStatic<Properties> utilities = Mockito.mockStatic(Properties.class)) {
      utilities.when(() -> Properties.getEnvValue("EVENT_SOURCE"))
          .thenReturn(
              "arn:aws:sqs:us-east-1:465257512377:ESMP-X-D-RMCS-RECEIVABLE-PAYMENT_NOTIFICATION_DEV02_QUEUE");
      SQSEvent sqsEvent = TestUtil.getSQSEvent("/ValidDMEvent.json");
      sqsEvent.getRecords().get(0).setMessageId("test");
      try (MockedStatic<Util> mockCommon = mockStatic(Util.class,
          InvocationOnMock::callRealMethod)) {
        when(Util.isSourceValid(sqsEvent)).thenReturn(true);
        Map<String, Object> response = receivablePaymentNotificationLambda.apply(sqsEvent);
        assertEquals(
            Constants.COMPLETION_STATUS_SUCCESS,
            response.get(Constants.COMPLETION_STATUS), "Unexpected Completion Status!");
        assertEquals(
            "Received a valid DM message, And Update to Receivable is Successful",
            response.get(Constants.COMPLETION_DETAILS), "Unexpected Completion Details!");
      }
    }
  }

  @Test
  public void testProcessSQSEvent_whenValidMessage_butException() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/ValidDMEvent.json");
    sqsEvent.getRecords().get(0).setMessageId("test");
    String expectMsg = "test Exception";
    doThrow(new ReceivablePaymentNotificationException(expectMsg))
        .when(receivablePaymentNotificationService)
        .updateReceivablePaymentStatus(any(), any());
    try (MockedStatic<Util> mockCommon = mockStatic(Util.class)) {
      when(Util.isSourceValid(sqsEvent)).thenReturn(true);
      when(Util.getMessageId(any(), any())).thenCallRealMethod();
      when(Util.getPaymentNoticeChangeEvent(sqsEvent)).thenCallRealMethod();
      Throwable exception =
          Assertions.assertThrows(
              ReceivablePaymentNotificationException.class,
              () -> receivablePaymentNotificationLambda.apply(sqsEvent));
      assertEquals(expectMsg, exception.getMessage());
    }
  }

  @Test
  public void testProcessSQSEvent_whenValidMessage_DryRun() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/DryRunDMEvent.json");
    try (MockedStatic<Util> mockCommon = mockStatic(Util.class)) {
      when(Util.isDryRun(sqsEvent)).thenReturn(true);
      Map<String, Object> response = receivablePaymentNotificationLambda.apply(sqsEvent);
      assertEquals(
          "success",
          response.get(Constants.DRY_RUN), Constants.DRY_RUN);
    }
  }
}