package org.finra.rmcs.service;

import org.finra.herd.sdk.model.BusinessObjectData;
import org.finra.herd.sdk.model.BusinessObjectDataKey;
import org.finra.rmcs.exception.RetryableException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

public interface HerdService {

  @Retryable(
      value = RetryableException.class,
      maxAttemptsExpression = "${retry.maxAttempts}",
      backoff =
          @Backoff(
              delayExpression = "${retry.initialInterval}",
              multiplierExpression = "${retry.multiplierFactor}"))
  BusinessObjectData getBusinessObjectData(BusinessObjectDataKey businessObjectDataKey);
}
