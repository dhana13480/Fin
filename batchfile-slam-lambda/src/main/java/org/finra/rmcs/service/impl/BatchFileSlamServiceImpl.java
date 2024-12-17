package org.finra.rmcs.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.service.BatchFileSlamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Service
@Slf4j
public class BatchFileSlamServiceImpl implements BatchFileSlamService {

  @Value("${slam.topic.arn}")
  private String slamTopicArn;

  private final SnsClient snsClient;

  @Autowired
  public BatchFileSlamServiceImpl(SnsClient snsClient) {
    this.snsClient = snsClient;
  }

  @Override
  public void sendSlamEvent(String event) {
    PublishRequest request = PublishRequest.builder().message(event).topicArn(slamTopicArn).build();
    PublishResponse result = snsClient.publish(request);
    log.info(
        String.format(
            "Published SLAM event to topic %s with message id %s and message: %s",
            slamTopicArn, result.messageId(), event));
  }
}
