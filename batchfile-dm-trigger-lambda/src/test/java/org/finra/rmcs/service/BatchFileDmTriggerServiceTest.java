package org.finra.rmcs.service;

import static org.mockito.ArgumentMatchers.any;

import org.finra.rmcs.service.impl.BatchFileDmTriggerServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.StartExecutionRequest;
import software.amazon.awssdk.services.sfn.model.StartExecutionResponse;

@SpringJUnitConfig
public class BatchFileDmTriggerServiceTest {

  @InjectMocks
  private BatchFileDmTriggerServiceImpl batchFileDmTriggerService;
  @Mock
  private SfnClient sfnClient;

  @Test
  public void testTriggerStepFunction() {
    Mockito.when(sfnClient.startExecution(any(StartExecutionRequest.class)))
        .thenReturn(StartExecutionResponse.builder().build());
    batchFileDmTriggerService.triggerStepFunction("test", "test");
    Mockito.verify(sfnClient).startExecution(any(StartExecutionRequest.class));
  }
}
