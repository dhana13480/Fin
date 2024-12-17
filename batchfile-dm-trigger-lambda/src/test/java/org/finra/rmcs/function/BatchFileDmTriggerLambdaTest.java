package org.finra.rmcs.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import java.util.Map;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.exception.BatchFileDmTriggerException;
import org.finra.rmcs.service.impl.BatchFileDmTriggerServiceImpl;
import org.finra.rmcs.util.TestUtil;
import org.finra.rmcs.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

@SpringJUnitConfig
public class BatchFileDmTriggerLambdaTest {

  @Mock
  private BatchFileDmTriggerServiceImpl batchFileDmTriggerServiceImpl;
  @InjectMocks
  private BatchFileDmTriggerLambda batchFileDmTriggerLambda;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(
        batchFileDmTriggerLambda,
        "stepFunctionARN",
        "arn:aws:states:us-east-1:465257512377:stateMachine:RMCS-Receivables-Batch-File-Process-DEV-v1");
  }

  @Test
  public void testProcessSQSEvent_whenNonMessageAttributes_skip() throws Exception {
    try (MockedStatic<Properties> utilities = Mockito.mockStatic(Properties.class)) {
      utilities.when(() -> Properties.getEnvValue(Constants.EVENT_SOURCE))
          .thenReturn(
              "arn:aws:sqs:us-east-1:465257512377:ESMP-X-D-RMCS-RECEIVABLE-DM_MESSAGE_QUEUE");
      SQSEvent sqsEvent = TestUtil.getSQSEvent("/NonSNSMessage.json");
      sqsEvent.getRecords().get(0).setMessageId("test");
      sqsEvent.getRecords().get(0).setEventSourceArn(
          "arn:aws:sqs:us-east-1:465257512377:ESMP-X-D-RMCS-RECEIVABLE-DM_MESSAGE_QUEUE");
      sqsEvent.getRecords().get(0).setEventSource("aws:sqs");
      try (MockedStatic<Util> mockCommon = mockStatic(Util.class,
          InvocationOnMock::callRealMethod)) {
        when(Util.isSourceValid(sqsEvent)).thenReturn(true);
        Map<String, Object> response = batchFileDmTriggerLambda.apply(sqsEvent);
        assertEquals(Constants.COMPLETION_STATUS_SKIP, response.get(Constants.COMPLETION_STATUS));
        assertEquals(
            Constants.COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_ATTRIBUTES_NOT_FOUND,
            response.get(Constants.COMPLETION_DETAILS));
      }
    }
  }

  @Test
  public void testProcessSQSEvent_whenNonMessageId_skip() throws Exception {
    try (MockedStatic<Properties> utilities = Mockito.mockStatic(Properties.class)) {
      utilities.when(() -> Properties.getEnvValue(Constants.EVENT_SOURCE))
          .thenReturn(
              "arn:aws:sqs:us-east-1:465257512377:ESMP-X-D-RMCS-RECEIVABLE-DM_MESSAGE_QUEUE");
      SQSEvent sqsEvent = TestUtil.getSQSEvent("/MissingMessageId.json");
      sqsEvent.getRecords().get(0).setMessageId("test");
      sqsEvent.getRecords().get(0).setEventSourceArn(
          "arn:aws:sqs:us-east-1:465257512377:ESMP-X-D-RMCS-RECEIVABLE-DM_MESSAGE_QUEUE");
      sqsEvent.getRecords().get(0).setEventSource("aws:sqs");
      try (MockedStatic<Util> mockCommon = mockStatic(Util.class,
          InvocationOnMock::callRealMethod)) {
        when(Util.isSourceValid(sqsEvent)).thenReturn(true);
        Map<String, Object> response = batchFileDmTriggerLambda.apply(sqsEvent);
        assertEquals(Constants.COMPLETION_STATUS_SKIP, response.get(Constants.COMPLETION_STATUS));
        assertEquals(
            Constants.COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_MESSAGE_ID_NOT_FOUND,
            response.get(Constants.COMPLETION_DETAILS));
      }
    }
  }

  @Test
  public void testProcessSQSEvent_whenInvalidMessage_skip() throws Exception {
    try (MockedStatic<Properties> utilities = Mockito.mockStatic(Properties.class)) {
      utilities.when(() -> Properties.getEnvValue(Constants.EVENT_SOURCE))
          .thenReturn(
              "arn:aws:sqs:us-east-1:465257512377:ESMP-X-D-RMCS-RECEIVABLE-DM_MESSAGE_QUEUE");

      SQSEvent sqsEvent = TestUtil.getSQSEvent("/InvalidDMEvent.json");
      sqsEvent.getRecords().get(0).setMessageId("test");
      sqsEvent.getRecords().get(0).setEventSourceArn(
          "arn:aws:sqs:us-east-1:465257512377:ESMP-X-D-RMCS-RECEIVABLE-DM_MESSAGE_QUEUE");
      sqsEvent.getRecords().get(0).setEventSource("aws:sqs");
      try (MockedStatic<Util> mockCommon = mockStatic(Util.class,
          InvocationOnMock::callRealMethod)) {
        when(Util.isSourceValid(sqsEvent)).thenReturn(true);
        Map<String, Object> response = batchFileDmTriggerLambda.apply(sqsEvent);
        assertEquals(
            Constants.COMPLETION_STATUS_SKIP,
            response.get(Constants.COMPLETION_STATUS), "Unexpected Completion Status!");
        assertEquals(
            Constants.COMPLETION_DETAILS_SKIP_SQS_EVENT,
            response.get(Constants.COMPLETION_DETAILS), "Unexpected Completion Details!");
      }
    }
  }

  @Test
  public void testProcessSQSEvent_whenValidMessage() throws Exception {
    try (MockedStatic<Properties> utilities = Mockito.mockStatic(Properties.class)) {
      utilities.when(() -> Properties.getEnvValue(Constants.EVENT_SOURCE))
          .thenReturn(
              "arn:aws:sqs:us-east-1:465257512377:ESMP-X-D-RMCS-RECEIVABLE-DM_MESSAGE_QUEUE");
      SQSEvent sqsEvent = TestUtil.getSQSEvent("/ValidDMEvent.json");
      sqsEvent.getRecords().get(0).setMessageId("test");
      try (MockedStatic<Util> mockCommon = mockStatic(Util.class,
          InvocationOnMock::callRealMethod)) {
        when(Util.isSourceValid(sqsEvent)).thenReturn(true);
        Map<String, Object> response = batchFileDmTriggerLambda.apply(sqsEvent);
        assertEquals(
            Constants.COMPLETION_STATUS_SUCCESS,
            response.get(Constants.COMPLETION_STATUS), "Unexpected Completion Status!");
        assertEquals(
            "Received a valid DM message Received a valid DM event for RMCS-APIBI-RECEIVABLES-IN version 0 with the partition values 2023-03-13 58450e2f-d502-413d-a9da-4cadbf5fdf55, And passed it to Step Function successfully",
            response.get(Constants.COMPLETION_DETAILS), "Unexpected Completion Details!");
      }
    }
  }

  @Test
  public void testProcessSQSEvent_whenValidMessage_butException() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/ValidDMEvent.json");
    sqsEvent.getRecords().get(0).setMessageId("test");
    String expectMsg = "test Exception";
    Mockito.doThrow(new BatchFileDmTriggerException(expectMsg))
        .when(batchFileDmTriggerServiceImpl)
        .triggerStepFunction(any(), any());
    try (MockedStatic<Util> mockCommon = mockStatic(Util.class)) {
      when(Util.isSourceValid(sqsEvent)).thenReturn(true);
      when(Util.isMessageValid(sqsEvent)).thenCallRealMethod();
      when(Util.getMessageId(sqsEvent)).thenCallRealMethod();
      when(Util.getBusinessObjectDataStatusChangeEvent(sqsEvent)).thenCallRealMethod();
      Throwable exception =
          assertThrows(
              BatchFileDmTriggerException.class, () -> batchFileDmTriggerLambda.apply(sqsEvent));
      assertEquals(expectMsg, exception.getMessage());
    }
  }

  @Test
  public void testProcessSQSEvent_whenValidMessage_DryRun() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/DryRunDMEvent.json");
    Map<String, Object> response = batchFileDmTriggerLambda.apply(sqsEvent);
    assertEquals(
        "success",
        response.get(Constants.DRY_RUN), Constants.DRY_RUN);
  }
}