package org.finra.rmcs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import kong.unirest.HttpMethod;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.MockClient;
import org.finra.rmcs.FileReaderUtilTest;
import org.finra.rmcs.common.service.email.EmailServiceImpl;
import org.finra.rmcs.common.service.oauth2.OAuth2ServiceImpl;
import org.finra.rmcs.exception.RetryableException;
import org.finra.rmcs.exception.UnRetryableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

@SpringJUnitConfig
public class InvokeALBAuthenticateServiceImplTest {

  static Map<String, Object> request;
  @Mock
  OAuth2ServiceImpl oAuth2Service;
  @Mock
  EmailServiceImpl emailService;
  @InjectMocks
  @Spy
  InvokeALBAuthenticateServiceImpl invokeALBAuthenticateServiceImpl;

  @BeforeEach
  public void init() throws IOException {
    ReflectionTestUtils.setField(invokeALBAuthenticateServiceImpl, "gatewayUrl", "test");
    ReflectionTestUtils.setField(
        invokeALBAuthenticateServiceImpl,
        "errorEmailSubject",
        "[%s] - Error happened in processing batch file");
    ReflectionTestUtils.setField(
        invokeALBAuthenticateServiceImpl,
        "errorEmailBody",
        "%s happened an error in processing batch file due to the following issue, please investigate it. Thanks!\\n\\nerror_message: %s\\n\\ntransmission_id: %s\\nsns_message_id: %s\\ns3_url: %s\\nstack trace: \\n%s");
    ReflectionTestUtils.setField(
        invokeALBAuthenticateServiceImpl, "errorEmailFrom", "test@finra.org");
    ReflectionTestUtils.setField(
        invokeALBAuthenticateServiceImpl, "errorEmailTo", "test@finra.org");
    ReflectionTestUtils.setField(
        invokeALBAuthenticateServiceImpl, "errorEmailCC", "test@finra.org");
    request =
        new ObjectMapper()
            .readerFor(Object.class)
            .readValue(FileReaderUtilTest.getResourceContent("ProcessReceivableRequest.json"));
  }

  @Test
  public void testSendErrorNotificationEmail_whenJsonLineCountMisMatchException() {
    invokeALBAuthenticateServiceImpl.sendErrorNotificationEmail(
        new UnRetryableException("testException"), "testS3", "testSNS", "testId");
    verify(emailService).sendEMail(anyString(), anyString(), anyString(), anyString(), anyString());
  }

  @Test
  public void testSendErrorNotificationEmail_whenUnexpectedException() {
    invokeALBAuthenticateServiceImpl.sendErrorNotificationEmail(
        new RuntimeException("testException"), "testS3", "testSNS", "testId");
    verify(emailService).sendEMail(anyString(), anyString(), anyString(), anyString(), anyString());
  }

  @Test
  public void testRecoverAuthenticateALB() {
    RetryableException retryableException =
        new RetryableException("Failed to invoke Api Gateway due to 500 series error");
    Throwable exception =
        assertThrows(
            UnRetryableException.class,
            () ->
                invokeALBAuthenticateServiceImpl.recoverAuthenticateALB(
                    retryableException, new HashMap<>(), "testId"));
    assertEquals(
        "Reached maximum retry times, Failed to invoke Api Gateway due to 500 series error",
        exception.getMessage());
  }

  @Test
  public void testAuthenticateALB() {
    MockClient mock = MockClient.register();
    JsonNode responseBody = new JsonNode("{\"test\":\"test\"}");
    mock.expect(HttpMethod.POST, "test").thenReturn(responseBody.toString());
    when(oAuth2Service.getAccessToken()).thenReturn("test");
    HttpResponse<JsonNode> response =
        invokeALBAuthenticateServiceImpl.authenticateALB(request, "testS3");
    mock.verifyAll();
    assertEquals(responseBody.toPrettyString(), response.getBody().toPrettyString());
  }

  @Test()
  public void testAuthenticateALB_RetryableException() {
    MockClient mock = MockClient.register();
    JsonNode responseBody = new JsonNode("{\"test\":\"test\"}");
    mock.expect(HttpMethod.POST, "test").thenReturn(responseBody.toString()).withStatus(500);
    assertThrows(
        RetryableException.class,
        () -> invokeALBAuthenticateServiceImpl.authenticateALB(request, "testS3"));
    mock.verifyAll();
  }
}
