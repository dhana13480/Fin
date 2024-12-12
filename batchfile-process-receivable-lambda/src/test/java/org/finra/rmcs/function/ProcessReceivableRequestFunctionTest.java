package org.finra.rmcs.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import org.finra.rmcs.FileReaderUtilTest;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.dto.Receivable;
import org.finra.rmcs.exception.UnRetryableException;
import org.finra.rmcs.service.InvokeALBAuthenticateService;
import org.finra.rmcs.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class ProcessReceivableRequestFunctionTest {

  private Map<String, Object> request;
  @InjectMocks private ProcessReceivableRequestFunction processReceivableRequestFunction;

  @Mock private InvokeALBAuthenticateService invokeALBAuthenticateService;

  @Mock private S3Service s3Service;

  @Mock private HttpResponse<JsonNode> httpResponse;

  @Mock private ObjectMapper objectMapper;

  @BeforeEach
  public void initReceivables() throws IOException {
    request =
        new ObjectMapper()
            .readerFor(Object.class)
            .readValue(FileReaderUtilTest.getResourceContent("ProcessReceivableRequest.json"));
  }

  @Test
  public void applySuccessOKTest() throws JsonProcessingException {
    Receivable receivable = new Receivable();
    receivable.setSource("test");
    receivable.setId("id");
    when(objectMapper.readValue(request.get("receivable").toString(), Receivable.class))
        .thenReturn(receivable);
    when(s3Service.retrieveS3ObjectInRange(any(), any()))
        .thenReturn(request.get("receivable").toString());
    when(httpResponse.getStatus()).thenReturn(200);
    when(invokeALBAuthenticateService.authenticateALB(any(), any())).thenReturn(httpResponse);
    Map<String, Object> returnResponse = processReceivableRequestFunction.apply(request);
    assertEquals(Constants.COMPLETED, returnResponse.get(Constants.TYPE));
  }

  @Test
  public void applySuccessDryRunTest() {
    Map<String, Object> request = new HashMap<>();
    request.put(Constants.DRY_RUN, true);
    Map<String, Object> returnResponse = processReceivableRequestFunction.apply(request);
    assertEquals("success", returnResponse.get(Constants.DRY_RUN));
  }

  @Test
  public void applyBadRequestTest() throws JsonProcessingException {
    Receivable receivable = new Receivable();
    receivable.setSource("test");
    receivable.setId("id");
    when(httpResponse.getStatus()).thenReturn(400);
    when(objectMapper.readValue(request.get("receivable").toString(), Receivable.class))
        .thenReturn(receivable);
    when(s3Service.retrieveS3ObjectInRange(any(), any()))
        .thenReturn(request.get("receivable").toString());
    when(invokeALBAuthenticateService.authenticateALB(any(), any())).thenReturn(httpResponse);
    Map<String, Object> returnResponse = processReceivableRequestFunction.apply(request);
    assertEquals(Constants.ERROR, returnResponse.get(Constants.TYPE));
  }

  @Test
  public void testProcessReceivableRequestFunction_when500Error_thenSendingEmail()
      throws JsonProcessingException {
    String expectedMsg =
        "Reached maximum retry times, Failed to invoke Api Gateway due to 500 series error";
    Receivable receivable = new Receivable();
    receivable.setSource("test");
    receivable.setId("id");
    when(s3Service.retrieveS3ObjectInRange(any(), any()))
        .thenReturn(request.get("receivable").toString());
    when(objectMapper.readValue(request.get("receivable").toString(), Receivable.class))
        .thenReturn(receivable);
    when(invokeALBAuthenticateService.authenticateALB(anyMap(), anyString()))
        .thenThrow(new UnRetryableException(expectedMsg));
    Throwable exception =
        assertThrows(
            UnRetryableException.class, () -> processReceivableRequestFunction.apply(request));
    assertEquals(expectedMsg, exception.getMessage());
    Mockito.verify(invokeALBAuthenticateService)
        .sendErrorNotificationEmail(any(), anyString(), anyString(), anyString());
  }

  @Test
  public void testProcessReceivableRequestFunction_whenUnexpectedException_thenSendingEmail()
      throws JsonProcessingException {
    String expectedMsg = "testException";
    Receivable receivable = new Receivable();
    receivable.setSource("test");
    receivable.setId("id");
    when(objectMapper.readValue(request.get("receivable").toString(), Receivable.class))
        .thenThrow(new RuntimeException(expectedMsg));
    when(s3Service.retrieveS3ObjectInRange(any(), any()))
        .thenReturn(request.get("receivable").toString());
    Throwable exception =
        assertThrows(RuntimeException.class, () -> processReceivableRequestFunction.apply(request));
    assertEquals(expectedMsg, exception.getMessage());
    Mockito.verify(invokeALBAuthenticateService)
        .sendErrorNotificationEmail(any(), anyString(), anyString(), anyString());
  }
}
