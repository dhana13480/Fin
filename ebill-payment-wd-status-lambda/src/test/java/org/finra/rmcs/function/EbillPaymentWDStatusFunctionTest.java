package org.finra.rmcs.function;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.model.NotificationMessage;
import org.finra.rmcs.service.impl.PaymentTrackingWDStatusServiceImpl;
import org.finra.rmcs.utils.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class EbillPaymentWDStatusFunctionTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  SQSEvent dryRunSqsEvent;
  @Mock
  PaymentTrackingWDStatusServiceImpl paymentTrackingWDStatusService;
  @InjectMocks
  private EbillPaymentWDStatusFunction ebillPaymentWDStatusFunction;

  @BeforeEach
  public void init() throws Exception {
    dryRunSqsEvent = new SQSEvent();
    List<SQSMessage> sqsMessages = new ArrayList<>();
    SQSMessage sqsMessage = new SQSMessage();
    sqsMessage.setBody(TestUtil.getResourceFileContents("/DryRun.json"));
    sqsMessages.add(sqsMessage);
    dryRunSqsEvent.setRecords(sqsMessages);

  }

  @Test
  void apply_dryRunTest() {
    Map<String, Object> actual = ebillPaymentWDStatusFunction.apply(dryRunSqsEvent);
    Assertions.assertEquals("success", actual.get(Constants.DRY_RUN));
  }

  @Test
  void apply_dryRunFailTest() throws Exception {
    dryRunSqsEvent = new SQSEvent();
    List<SQSMessage> sqsMessages = new ArrayList<>();
    SQSMessage sqsMessage = new SQSMessage();
    sqsMessage.setBody(TestUtil.getResourceFileContents("/DryRunFalse.json"));
    sqsMessages.add(sqsMessage);
    dryRunSqsEvent.setRecords(sqsMessages);
    Map<String, Object> actual = ebillPaymentWDStatusFunction.apply(dryRunSqsEvent);
    Assertions.assertNull(actual.get(Constants.DRY_RUN));
  }


  @Test
  void apply_validMessageTest() throws IOException {
    SQSEvent sqsEvent =
        mapper.readValue(
            this.getClass().getClassLoader().getResourceAsStream("ValidEvent.json"),
            SQSEvent.class);
    NotificationMessage notificationMessage = new NotificationMessage();
    Map<String, Object> actual = ebillPaymentWDStatusFunction.apply(sqsEvent);
    Assertions.assertEquals(Constants.COMPLETION_STATUS_SUCCESS,
        actual.get(Constants.COMPLETION_STATUS));
    Assertions.assertEquals(Constants.COMPLETION_SUCCESS_DETAILS,
        actual.get(Constants.COMPLETION_DETAILS));
  }

  @Test
  void apply_inValidMessageIdTest() throws IOException {
    SQSEvent sqsEvent =
        mapper.readValue(
            this.getClass().getClassLoader().getResourceAsStream("InValidMessageIdEvent.json"),
            SQSEvent.class);
    Map<String, Object> actual = ebillPaymentWDStatusFunction.apply(sqsEvent);
    Assertions.assertEquals(Constants.COMPLETION_STATUS_SKIP,
        actual.get(Constants.COMPLETION_STATUS));
    Assertions.assertEquals(Constants.COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_MESSAGE_ID_NOT_FOUND,
        actual.get(Constants.COMPLETION_DETAILS));
  }

  @Test()
  void apply_exceptionMessageTest() throws IOException {
    SQSEvent sqsEvent =
        mapper.readValue(
            this.getClass().getClassLoader().getResourceAsStream("ExceptionEvent.json"),
            SQSEvent.class);
    Map<String, Object> actual = ebillPaymentWDStatusFunction.apply(sqsEvent);
    Assertions.assertEquals(Constants.COMPLETION_STATUS_FAILED,
        actual.get(Constants.COMPLETION_STATUS));
    Assertions.assertEquals(Constants.COMPLETION_FAILED_DETAILS,
        actual.get(Constants.COMPLETION_DETAILS));

  }

}
