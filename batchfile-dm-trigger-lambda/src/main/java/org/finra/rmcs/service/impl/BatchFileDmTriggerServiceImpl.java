package org.finra.rmcs.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.service.BatchFileDmTriggerService;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.StartExecutionRequest;

@Service
@Slf4j
@AllArgsConstructor
public class BatchFileDmTriggerServiceImpl implements BatchFileDmTriggerService {

  private final SfnClient sfnClient;

  public void triggerStepFunction(String serializedInput, String sfnArn) {
    log.info(
        "BatchfileDmTriggerService.triggerStepFunction: serializedInput {}, sfnArn {}",
        serializedInput,
        sfnArn);

    StartExecutionRequest executionRequest =
        StartExecutionRequest.builder().input(serializedInput).stateMachineArn(sfnArn).build();

    sfnClient.startExecution(executionRequest);
  }
}
