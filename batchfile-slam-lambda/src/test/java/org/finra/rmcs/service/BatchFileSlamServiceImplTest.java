package org.finra.rmcs.service;

import static org.mockito.ArgumentMatchers.any;

import org.finra.rmcs.service.impl.BatchFileSlamServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@ExtendWith(MockitoExtension.class)
public class BatchFileSlamServiceImplTest {

  @InjectMocks
  private BatchFileSlamServiceImpl batchFileSlamService;
  @Mock
  private SnsClient snsClient;

  @BeforeEach
  public void before() {
    ReflectionTestUtils.setField(batchFileSlamService, "slamTopicArn", "testTopicArn");
  }

  @Test
  public void testSendSlamEvent() {
    Mockito.when(snsClient.publish(any(PublishRequest.class)))
        .thenReturn(PublishResponse.builder().messageId("testMessageId").build());
    batchFileSlamService.sendSlamEvent("testEvent");
    Mockito.verify(snsClient).publish(any(PublishRequest.class));
  }
}
