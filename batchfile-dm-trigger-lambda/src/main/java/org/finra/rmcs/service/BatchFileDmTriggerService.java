package org.finra.rmcs.service;

public interface BatchFileDmTriggerService {

  void triggerStepFunction(String serializedInput, String sfnArn);
}
