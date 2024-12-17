package org.finra.rmcs.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import org.finra.rmcs.dto.GatewayEmailNotificationRequest;
import org.finra.rmcs.dto.GatewayEmailNotificationResponse;
import org.finra.rmcs.entity.EmailConfig;
import org.finra.rmcs.service.impl.GatewayEmailNotificationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@SpringJUnitConfig
public class GatewayEmailNotificationServiceImplTest {

  @InjectMocks
  GatewayEmailNotificationServiceImpl gatewayEmailNotificationServiceImpl;

  @Mock
  RetryTemplate retryTemplate;

  @Mock
  RestTemplate restTemplate;

  @Mock
  ObjectMapper objectMapper;
  GatewayEmailNotificationRequest request;
  @Value("${dxtn.fcn.url}")
  private String dxtnFcnUrl;
  @Value("${dxtn.token.url}")
  private String dxtnApiUrl;
  @Value("${dxtn.fcn.username}")
  private String dxtnUser;
  @Value("${spring.oauth2.client.grantType}")
  private String grantType;

  @BeforeEach
  void setup() {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(gatewayEmailNotificationServiceImpl, "dxtnFcnUrl", "dxtnFcnUrl");
    ReflectionTestUtils.setField(gatewayEmailNotificationServiceImpl, "dxtnApiUrl", "dxtnApiUrl");
    ReflectionTestUtils.setField(gatewayEmailNotificationServiceImpl, "dxtnUser", "dxtnUser");
    ReflectionTestUtils.setField(gatewayEmailNotificationServiceImpl, "grantType", "grantType");

    request = new GatewayEmailNotificationRequest();
    request.setTo(new ArrayList<>(Arrays.asList("test@test.com")));
    request.setBody("Test Body");
    request.setSubject("Test Subject");
    request.setEventName("Event Name");
    request.setFeedback("Test Feedback");

  }

  @Test
  public void sendGatewayEmailNotificationTest() {
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setTo("abc@finra.org");
    GatewayEmailNotificationResponse[] response = new GatewayEmailNotificationResponse[1];
    response[0] = new GatewayEmailNotificationResponse(new String[]{"test"}, "123");
    ResponseEntity<GatewayEmailNotificationResponse[]> responseEntity = ResponseEntity.status(
        HttpStatus.CREATED).body(response);
    Mockito.when(retryTemplate.execute(any())).thenAnswer(invocation -> {
      RetryCallback retry = invocation.getArgument(0);
      return retry.doWithRetry(null);
    });
    Mockito.when(restTemplate.exchange(anyString(), any(), any(),
        eq(GatewayEmailNotificationResponse[].class))).thenReturn(responseEntity);

    GatewayEmailNotificationResponse[] actual = gatewayEmailNotificationServiceImpl.sendGatewayEmailNotification(
        request, emailConfig);
    Assertions.assertEquals("123", actual[0].getId());
  }

  @Test
  public void sendGatewayEmailNotificationTest_Exception() {
    EmailConfig emailConfig = new EmailConfig();
    Mockito.when(retryTemplate.execute(any())).thenReturn(new Exception());

    GatewayEmailNotificationResponse[] actual = gatewayEmailNotificationServiceImpl.sendGatewayEmailNotification(
        request, emailConfig);
    Assertions.assertNull(actual);
  }
}