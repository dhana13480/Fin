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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.finra.herd.sdk.model.BusinessObjectDataKey;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

@ExtendWith(SystemStubsExtension.class)
public class UtilTest {

  static {
    System.setProperty(Constants.EVENT_SOURCE,
        "arn:aws:sqs:us-east-1:465257512377:ESMP-X-D-RMCS-RECEIVABLE-DM_MESSAGE_QUEUE");
  }

  @SystemStub
  private EnvironmentVariables environment = new EnvironmentVariables(Constants.EVENT_SOURCE, "arn:aws:sqs:us-east-1:465257512377:ESMP-X-D-RMCS-RECEIVABLE-DM_MESSAGE_QUEUE");
  @Test
  public void testGetMessageIdNotNull() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/ValidDMEvent.json");
    String expected = "c7141a29-c544-5550-98d6-46a98f562413";
    String actual = Util.getMessageId(sqsEvent);
    assertEquals(expected, actual);
  }

  @Test
  public void testGetMessageIdNull() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/MissingMessageId.json");
    assertNull(Util.getMessageId(sqsEvent));
  }

  @Test
  public void testGetMessageIdNonSNSMessage() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/NonSNSMessage.json");
    assertNull(Util.getMessageId(sqsEvent));
  }

  @Test
  public void testIsMessageValid_whenNotAJson() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/NonJson.json");
    assertFalse(Util.isMessageValid(sqsEvent));
  }

  @Test
  public void testIsMessageValid_whenNoMessageAttributes() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/NonSNSMessage.json");
    assertFalse(Util.isMessageValid(sqsEvent));
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
  public void testGetBusinessObjectDataStatusChangeEvent() throws Exception {
    List<String> expectedSubPartitionValues = new ArrayList<>();
    expectedSubPartitionValues.add("58450e2f-d502-413d-a9da-4cadbf5fdf55");
    BusinessObjectDataKey expectedDataKey = new BusinessObjectDataKey();
    expectedDataKey.setNamespace("RMCS-APIBI");
    expectedDataKey.setBusinessObjectDefinitionName("RMCS-APIBI-RECEIVABLES-IN");
    expectedDataKey.setBusinessObjectFormatUsage("BILLING");
    expectedDataKey.setBusinessObjectFormatFileType("JSONL");
    expectedDataKey.setBusinessObjectFormatVersion(0);
    expectedDataKey.setPartitionValue("2023-03-13");
    expectedDataKey.setSubPartitionValues(expectedSubPartitionValues);
    expectedDataKey.setBusinessObjectDataVersion(0);
    Map<String, String> expectedAttributes = new HashMap<>();
    expectedAttributes.put("revenue_stream", "APIBI");
    BusinessObjectDataStatusChangeEvent expected = new BusinessObjectDataStatusChangeEvent();
    expected.setBusinessObjectDataKey(expectedDataKey);
    expected.setEventDate("2023-03-06T15:06:42.924-05:00");
    expected.setNewBusinessObjectDataStatus("VALID");
    expected.setOldBusinessObjectDataStatus("UPLOADING");
    expected.setAttributes(expectedAttributes);
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/ValidDMEvent.json");
    BusinessObjectDataStatusChangeEvent actual =
        Util.getBusinessObjectDataStatusChangeEvent(sqsEvent);
    assertEquals(expected, actual);
  }

  @Test
  public void testisSourceValid_whenNotValidSource() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/InvalidDMEvent.json");
    assertFalse(Util.isSourceValid(sqsEvent));
  }

  @Test
  public void testisSourceValid_whenValidSource() throws Exception {
    SQSEvent sqsEvent = TestUtil.getSQSEvent("/ValidDMEvent.json");
    assertTrue(Util.isSourceValid(sqsEvent));
  }
}
