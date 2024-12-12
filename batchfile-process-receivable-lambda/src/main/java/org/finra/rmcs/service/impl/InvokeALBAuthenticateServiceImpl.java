package org.finra.rmcs.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.common.service.email.EmailServiceImpl;
import org.finra.rmcs.common.service.oauth2.OAuth2ServiceImpl;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.exception.RetryableException;
import org.finra.rmcs.exception.UnRetryableException;
import org.finra.rmcs.service.InvokeALBAuthenticateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InvokeALBAuthenticateServiceImpl implements InvokeALBAuthenticateService {

  private final OAuth2ServiceImpl oAuth2Service;
  private final EmailServiceImpl emailService;
  @Value("${email.error.subject}")
  private String errorEmailSubject;
  @Value("${email.error.body}")
  private String errorEmailBody;
  @Value("${email.error.from}")
  private String errorEmailFrom;
  @Value("${email.error.to}")
  private String errorEmailTo;
  @Value("${email.error.cc}")
  private String errorEmailCC;
  @Value("${spring.api.gateway.url}")
  private String gatewayUrl;

  @Autowired
  public InvokeALBAuthenticateServiceImpl(
      OAuth2ServiceImpl oAuth2Service, EmailServiceImpl emailService) {
    this.oAuth2Service = oAuth2Service;
    this.emailService = emailService;
  }

  @Override
  @SneakyThrows
  public HttpResponse<JsonNode> authenticateALB(
      Map<String, Object> requestEvent, String correlationId) {
    String methodName =
        Constants.CLASS
            + this.getClass().getSimpleName()
            + " "
            + Constants.METHOD
            + Thread.currentThread().getStackTrace()[1].getMethodName()
            + " "
            + Constants.CORRELATION_ID
            + correlationId;

    String requestBody = new ObjectMapper().writeValueAsString(requestEvent);
    log.info(
        "{} message : calling RMCS ALB to authenticate for receivableRequest {} ",
        methodName,
        requestBody);

    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put(
        Constants.AUTHORIZATION,
        String.format("%s %s", Constants.BEARER, oAuth2Service.getAccessToken()));

    HttpResponse<JsonNode> authResponse =
        Unirest.post(gatewayUrl).headers(headers).body(requestBody).asJson();

    if (HttpStatus.valueOf(authResponse.getStatus()).is5xxServerError()) {
      throw new RetryableException("Failed to invoke Api Gateway due to 500 series error");
    }

    String responseJsonString = authResponse.getBody().toString();
    log.info(
        "{} message : Response from UniRest Call to Validate Receivable item in ALB {}",
        methodName,
        responseJsonString);

    return authResponse;
  }

  @Override
  public void sendErrorNotificationEmail(
      Exception e, String s3Url, String snsMessageId, String transmissionId) {
    String subject;
    String body;
    if (e instanceof UnRetryableException) {
      subject =
          String.format(
              errorEmailSubject, System.getenv(Constants.SPRING_PROFILES_ACTIVE).toUpperCase());
      body =
          String.format(
              errorEmailBody,
              Constants.LAMBDA_NAME,
              e.getMessage(),
              transmissionId,
              snsMessageId,
              s3Url,
              Arrays.toString(e.getStackTrace()));
    } else {
      subject =
          String.format(
              "[%s] - Unexpected error occurred",
              System.getenv(Constants.SPRING_PROFILES_ACTIVE).toUpperCase());
      body =
          String.format(
              "%s happened an unexpected error occurred due to the following error, please investigate it. Thanks!%n%nerror_message: %s%n%ntransmission_id: %s%nsns_message_id: %s%ns3_url: %s%nstack trace: %n%s",
              Constants.LAMBDA_NAME,
              e.getMessage(),
              transmissionId,
              snsMessageId,
              s3Url,
              Arrays.toString(e.getStackTrace()));
    }
    emailService.sendEMail(errorEmailFrom, errorEmailTo, errorEmailCC, subject, body);
  }

  public HttpResponse<JsonNode> recoverAuthenticateALB(
      RetryableException retryableException,
      Map<String, Object> requestEvent,
      String correlationId) {
    throw new UnRetryableException(
        String.format("Reached maximum retry times, %s", retryableException.getMessage()));
  }
}
