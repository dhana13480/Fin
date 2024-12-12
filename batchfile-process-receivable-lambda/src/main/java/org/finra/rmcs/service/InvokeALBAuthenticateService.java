package org.finra.rmcs.service;

import java.util.Map;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import org.finra.rmcs.exception.RetryableException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

public interface InvokeALBAuthenticateService {
  @Retryable(
      recover = "recoverAuthenticateALB",
      value = RetryableException.class,
      maxAttemptsExpression = "${retry.maxAttempts}",
      backoff =
          @Backoff(
              delayExpression = "${retry.initialInterval}",
              multiplierExpression = "${retry.multiplierFactor}"))
  HttpResponse<JsonNode> authenticateALB(Map<String, Object> requestEvent, String correlationId);

  void sendErrorNotificationEmail(
      Exception e, String s3Url, String snsMessageId, String transmissionId);

  @Recover
  HttpResponse<JsonNode> recoverAuthenticateALB(
      RetryableException retryableException,
      Map<String, Object> requestEvent,
      String correlationId);
}
