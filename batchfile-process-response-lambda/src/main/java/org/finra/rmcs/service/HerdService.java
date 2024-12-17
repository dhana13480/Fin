package org.finra.rmcs.service;

import org.finra.herd.sdk.model.BusinessObjectData;
import org.finra.herd.sdk.model.BusinessObjectFormat;
import org.finra.rmcs.constants.HerdEntity;
import org.finra.rmcs.exception.RetryableException;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;
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
  BusinessObjectFormat getBusinessObjectFormat(
      BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent,
      HerdEntity herdEntity);

  @Retryable(
      value = RetryableException.class,
      maxAttemptsExpression = "${retry.maxAttempts}",
      backoff =
          @Backoff(
              delayExpression = "${retry.initialInterval}",
              multiplierExpression = "${retry.multiplierFactor}"))
  BusinessObjectData preRegisterBusinessObjectData(
      BusinessObjectFormat bizObjectFormat,
      BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent);

  @Retryable(
      value = RetryableException.class,
      maxAttemptsExpression = "${retry.maxAttempts}",
      backoff =
          @Backoff(
              delayExpression = "${retry.initialInterval}",
              multiplierExpression = "${retry.multiplierFactor}"))
  String uploadResponseFileToS3(BusinessObjectData bizObjData, String file, String fileName);

  @Retryable(
      value = RetryableException.class,
      maxAttemptsExpression = "${retry.maxAttempts}",
      backoff =
          @Backoff(
              delayExpression = "${retry.initialInterval}",
              multiplierExpression = "${retry.multiplierFactor}"))
  BusinessObjectData retrieveBusinessObjectData(
      BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent);

  @Retryable(
      value = RetryableException.class,
      maxAttemptsExpression = "${retry.maxAttempts}",
      backoff =
          @Backoff(
              delayExpression = "${retry.initialInterval}",
              multiplierExpression = "${retry.multiplierFactor}"))
  void updateBusinessObjectDataStorage(BusinessObjectData bizObjData);

  @Retryable(
      value = RetryableException.class,
      maxAttemptsExpression = "${retry.maxAttempts}",
      backoff =
          @Backoff(
              delayExpression = "${retry.initialInterval}",
              multiplierExpression = "${retry.multiplierFactor}"))
  String updateBusinessObjectDataStatus(BusinessObjectData businessObjectData, String status);
}
