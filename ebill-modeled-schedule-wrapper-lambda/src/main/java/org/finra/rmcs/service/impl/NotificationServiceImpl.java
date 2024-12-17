package org.finra.rmcs.service.impl;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.dto.NotificationMessage;
import org.finra.rmcs.service.NotificationService;
import org.finra.rmcs.utils.Utils;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
  private final  Utils utils;
  private final ObjectMapper objectMapper;
  private final AmazonSNS snsClient;

  @Value("${sns.internal.notification.topicArn}")
  private String paymentNotificationTopic;
  String methodName;

  @Override
  public void publishNotificationEvent(String correlationId, String module, String status) throws ServiceException {
    NotificationMessage snsMsg=null;
    try {
      if (StringUtils.isBlank(correlationId)) {
        throw new IllegalArgumentException("correlationId is null ");
      }
        snsMsg = utils.constructSNSMessage(correlationId, module, status);
        PublishRequest publishRequest = new PublishRequest().withTopicArn(paymentNotificationTopic)
                                           .withMessage(objectMapper.writeValueAsString(snsMsg));
        PublishResult result = snsClient.publish(publishRequest);
        log.info(String.format(
            "Published  notification event to topic %s with message id %s and message: %s",
            paymentNotificationTopic, result.getMessageId(), snsMsg));
    } catch (Exception e) {
      String message =String.format(
          "Error While Publishing  notification event to topic %s with message: %s failed, error: %s",
          paymentNotificationTopic, snsMsg, e);
      log.error("{} : Error   in  publishNotificationEvent() for userId [{}], module [{}],Error message [{}]",
          methodName, module, message);
          throw new RuntimeException(e);
        }
   }
}
