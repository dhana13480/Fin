package org.finra.rmcs.utils;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.finra.rmcs.model.NotificationMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;

@SpringJUnitConfig
class UtilTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  @InjectMocks
  Util utils;
  @Mock
  private Map<String, String> apiUserBean;
  @MockBean
  private RestTemplate restTemplate;

  @Test
  void getNotificationMessage_test() throws Exception {
    SQSEvent sqsEvent =
        mapper.readValue(
            this.getClass().getClassLoader().getResourceAsStream("ValidEvent.json"),
            SQSEvent.class);
    NotificationMessage actual = Util.getNotificationMessage(sqsEvent);
    Assertions.assertEquals("COMPLETED", actual.getStatus());
  }

  @Test
  void getMessageId_test() throws Exception {
    SQSEvent sqsEvent =
        mapper.readValue(
            this.getClass().getClassLoader().getResourceAsStream("ValidEvent.json"),
            SQSEvent.class);
    String actual = Util.getMessageId(sqsEvent, "1234-1ab23-sdd4-sadf");
    Assertions.assertEquals("1623e1f2-d627-52c5-b280-bc740bb89be6", actual);
  }

}


