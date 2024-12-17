package org.finra.rmcs.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.finra.rmcs.dto.NotificationMessage;
import org.finra.rmcs.utils.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

@SpringJUnitConfig
public class NotificationServiceImplTest {

  @InjectMocks
  private NotificationServiceImpl notificationService;
  @Mock
  private AmazonSNS snsClient;
  @Mock
  private Utils utils;
  @Mock
  private ObjectMapper objectMapper;

  private final String AFT="AFT";
  private final String SUBMITTED="SUBMITTED";
  private final String SUCCESS="SUCCESS";
  private final String EWSUSER="EWSUSER";
  private final String AftQueuedTransactions="AftQueuedTransactions";
  private final String correlation_id="correlation_id";
  @Value("${sns.internal.notification.topicArn}")
  private String paymentNotificationTopic;
  NotificationMessage notificationMessage=new NotificationMessage() ;
 @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(notificationService,"paymentNotificationTopic",paymentNotificationTopic);

    notificationMessage.setTransmissionId(correlation_id);
    notificationMessage.setModule(AFT);
    notificationMessage.setStatus(SUBMITTED);
    notificationMessage.setPaymentNumber(List.of("EBILL20230933747"));
  }

  @Test
  public void testPublishNotificationEventSuccess() throws Exception {

    when(objectMapper.writeValueAsString(any())).thenReturn(AFT);
    PublishResult publishResult = new PublishResult();
    publishResult.setMessageId("test");
    when(snsClient.publish(any())).thenReturn(publishResult);
    when(utils.constructSNSMessage(any(),any(),any())).thenReturn(notificationMessage);
    notificationService.publishNotificationEvent("test", AFT, SUBMITTED);
    verify(utils,times(1)).constructSNSMessage(any(),any(),any());
  }

  @Test
  public void testPublishNotificationEventBlankCorrelationId() throws Exception {
    Assertions.assertThrows(RuntimeException.class, () -> {
      notificationService.publishNotificationEvent(null, AFT, SUBMITTED);
    });
  }
  private List<String> getPaymentNumbers(){
    List<String> paymentNumberCollection = new ArrayList<>();
    paymentNumberCollection.add("EBILL20230933747");
    paymentNumberCollection.add("EBILL20230939793");
    return paymentNumberCollection;
  }

}