package org.finra.rmcs.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.dto.Receivable;
import org.finra.rmcs.exception.ProcessReceivableException;
import org.finra.rmcs.exception.UnRetryableException;
import org.finra.rmcs.service.InvokeALBAuthenticateService;
import org.finra.rmcs.service.S3Service;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessReceivableRequestFunction
    implements Function<Map<String, Object>, Map<String, Object>> {

  private final InvokeALBAuthenticateService invokeALBAuthenticateService;
  private final S3Service s3Service;
  private final ObjectMapper objectMapper;

  @SneakyThrows
  @Override
  public Map<String, Object> apply(Map<String, Object> requestEvent) {
    String correlationId = UUID.randomUUID().toString();
    String methodName =
        Constants.CLASS
            + this.getClass().getSimpleName()
            + " "
            + Constants.METHOD
            + Thread.currentThread().getStackTrace()[1].getMethodName()
            + " "
            + Constants.CORRELATION_ID
            + correlationId
            + " "
            + Constants.INPUT
            + requestEvent;
    log.info("{} message: method entry", methodName);
    Receivable receivable = null;

    boolean dryRun =
        Boolean.parseBoolean(
            requestEvent.getOrDefault(Constants.DRY_RUN, Constants.FALSE).toString());
    log.info("dryRun: {}", dryRun);
    if (dryRun) {
      log.info("dry run mode");
      requestEvent.put(Constants.DRY_RUN, "success");
      return requestEvent;
    }
    String transmissionId = requestEvent.get(Constants.TRANSMISSION_ID).toString();
    String snsMessageId = requestEvent.get(Constants.SNS_MESSAGE_ID).toString();
    String s3Url = requestEvent.get(Constants.FILE_URL).toString();
    String bytesRange = requestEvent.get(Constants.RECEIVABLE).toString();
    try {
      String receivableStr = s3Service.retrieveS3ObjectInRange(s3Url, bytesRange);
      receivable = objectMapper.readValue(receivableStr, Receivable.class);
      receivable.setSource(Constants.FILE_UPLOAD);
      receivable.setTransmissionId(transmissionId);
      requestEvent.put(Constants.RECEIVABLE, receivable);
      HttpResponse<JsonNode> authResponse =
          invokeALBAuthenticateService.authenticateALB(requestEvent, transmissionId);
      requestEvent.remove(Constants.RECEIVABLE);
      requestEvent.put(
          Constants.TYPE,
          HttpStatus.valueOf(authResponse.getStatus()).is2xxSuccessful()
              ? Constants.COMPLETED
              : Constants.ERROR);
      return requestEvent;
    } catch (UnRetryableException e) {
      log.error("UnRetryableException occurred {}", e.getMessage());
      invokeALBAuthenticateService.sendErrorNotificationEmail(
          e, s3Url, snsMessageId, transmissionId);
      throw new UnRetryableException(e.getMessage());
    } catch (Exception e) {
      log.error(
          "{} message:Exception occurred in ProcessReceivableRequestFunction  {} {}",
          methodName,
          e.getMessage(),
          e.getStackTrace());
      invokeALBAuthenticateService.sendErrorNotificationEmail(
          e, s3Url, snsMessageId, transmissionId);
      throw new ProcessReceivableException(e.getMessage());
    }
  }
}
