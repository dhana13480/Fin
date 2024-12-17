package org.finra.rmcs.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.model.PaymentNoticeChangeEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

@ExtendWith(SystemStubsExtension.class)
public class UtilTest {

  static {
    System.setProperty(Constants.EVENT_SOURCE, TestUtil.EVENT_SOURCE);
  }

  @SystemStub
  private EnvironmentVariables environment = new EnvironmentVariables(Constants.EVENT_SOURCE, TestUtil.EVENT_SOURCE);


  @Test
  public void testGetMessageIdNotNull() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/ValidDMEvent.json");
    String expected = "6de6e94a-7e41-402d-9e80-6800c6710977";
    String actual = Util.getMessageId(sqsEvent, "");
    assertEquals(expected, actual);
  }

  @Test
  public void testGetMessageIdNull() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/MissingMessageId.json");
    assertNull(Util.getMessageId(sqsEvent, ""));
  }

  @Test
  public void testGetMessageIdNonSNSMessage() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/NonSNSMessage.json");
    assertNull(Util.getMessageId(sqsEvent, ""));
  }

  @Test
  public void testPopulateReturnMap() {
    Map<String, Object> returnMap = mock(HashMap.class);
    Util.populateReturnMap(
        Constants.COMPLETION_STATUS_SUCCESS,
        Constants.COMPLETION_DETAILS_VALID_DM_MESSAGE,
        returnMap);
    verify(returnMap, times(2)).put(anyString(), anyString());
  }

  @Test
  public void testGetPaymentNoticeChangeEvent() throws Exception {
    PaymentNoticeChangeEvent expected = new PaymentNoticeChangeEvent();
    expected.setEventName("PAID");
    expected.setPaymentId(new ArrayList(Arrays.asList("92675d91-3aa8-42b6-a66a-68054df47465")));
    expected.setPaymentNumber(new ArrayList(Arrays.asList("payment_number")));
    expected.setPaymentReqId("1074eb54-4bf0-460a-b6aa-cde72303cea6");
    expected.setCustomerIds(new ArrayList(Arrays.asList("customerId")));
    expected.setInvoiceId(new ArrayList(Arrays.asList("MQPBI-12345")));
    expected.setProcessingRevenueStream("MQPBI");
    expected.setTransmissionId("d7e73b8d-1323-456f-a990-411086d3715c");
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/ValidDMEvent.json");
    PaymentNoticeChangeEvent actual = Util.getPaymentNoticeChangeEvent(sqsEvent);
    assertEquals(expected, actual);
  }

  // inject System environment variables is not working in Java17, comment the test case for now
  @Test
  public void testIsSourceValid_valid() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/PaidMessage.json", TestUtil.EVENT_SOURCE);
    assertTrue(Util.isSourceValid(sqsEvent));
  }

  @Test
  public void testIsSourceValid_invalid() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/PaidMessage.json", "wrongEventSourceArn");
    assertFalse(Util.isSourceValid(sqsEvent));
  }
}
