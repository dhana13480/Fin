package org.finra.rmcs.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.constants.HerdEntity;
import org.finra.rmcs.entity.ReceivableJsonFileEntity;
import org.finra.rmcs.exception.JsonLineCountMisMatchException;
import org.finra.rmcs.exception.ProcessResponseException;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;
import org.finra.rmcs.service.impl.ProcessResponseServiceImpl;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class ProcessResponseToDM implements Function<Map<String, Object>, Map<String, Object>> {

  private final ProcessResponseServiceImpl processResponseService;

  @Override
  public Map<String, Object> apply(Map<String, Object> stepFunctionInput) {
    UUID uuid = UUID.randomUUID();
    String correlationId = uuid.toString();

    String methodName =
        new StringBuilder()
            .append(Constants.CLASS)
            .append(this.getClass().getSimpleName())
            .append(" ")
            .append(Constants.METHOD)
            .append(Thread.currentThread().getStackTrace()[1].getMethodName())
            .append(" ")
            .append(Constants.CORRELATION_ID)
            .append(correlationId)
            .append(" ")
            .append(Constants.INPUT)
            .append(stepFunctionInput)
            .toString();
    log.info("{} message: method entry", methodName);

    String type;
    String jsonLineCount;
    String transmissionId = null;
    String s3Url = null;
    String snsMessageId = null;

    boolean dryRun =
        Boolean.parseBoolean(
            stepFunctionInput.getOrDefault(Constants.DRY_RUN, Constants.FALSE).toString());
    log.info("dryRun: {}", dryRun);
    if (dryRun) {
      log.info("dry run mode");
      stepFunctionInput.put(Constants.DRY_RUN, "success");
      return stepFunctionInput;
    }

    try {
      type = stepFunctionInput.getOrDefault(Constants.TYPE_KEY, "NON-FATAL").toString();
      transmissionId = stepFunctionInput.get(Constants.TRANSMISSION_ID_KEY).toString();
      jsonLineCount = stepFunctionInput.get(Constants.META_DATA_KEY_JSON_LINE_COUNT).toString();
      s3Url = stepFunctionInput.get(Constants.FILE_URL_KEY).toString();
      snsMessageId = stepFunctionInput.get(Constants.SNS_MESSAGE_ID_KEY).toString();
      log.info("Processing {} event for transmission_id {}", type, transmissionId);
      ObjectMapper objectMapper = new ObjectMapper();
      ReceivableJsonFileEntity receivableJsonFileEntity =
          processResponseService.getReceivableJsonEntityByTransmissionId(transmissionId);
      if (Integer.parseInt(jsonLineCount) == 0) {
        log.info("Empty file, skip processing response file for transmissionId {}", transmissionId);
        processResponseService.updateReceivableJsonEntityStatus(
            receivableJsonFileEntity, Collections.emptyMap(), Constants.STATUS_COMPLETED);
        stepFunctionInput.put(Constants.TYPE_KEY, Constants.STATUS_COMPLETED);
        return stepFunctionInput;
      }
      BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent =
          objectMapper.readValue(
              receivableJsonFileEntity.getDmDataStatusChangeEvent(),
              BusinessObjectDataStatusChangeEvent.class);
      Map<String, String> responseFileMap =
          processResponseService.createResponseFile(
              type,
              transmissionId,
              jsonLineCount,
              receivableJsonFileEntity,
              businessObjectDataStatusChangeEvent);
      responseFileMap.forEach(
          (fileName, responseFile) -> {
            if (StringUtils.isBlank(responseFile)) {
              throw new ProcessResponseException(
                  String.format("Response File is empty for file: %s!", fileName));
            }
            processResponseService.registerResponseFileToDM(
                fileName,
                responseFile,
                HerdEntity.findByFileName(fileName),
                businessObjectDataStatusChangeEvent);
          });

      processResponseService.updateReceivableJsonEntityStatus(
          receivableJsonFileEntity, responseFileMap, Constants.STATUS_COMPLETED);
      if (type.equalsIgnoreCase(Constants.STATUS_FATAL)) {
        processResponseService.sendErrorNotificationEmail(
            receivableJsonFileEntity.getMessage(), s3Url, snsMessageId, transmissionId);
      }
      stepFunctionInput.put(Constants.TYPE_KEY, Constants.STATUS_COMPLETED);
    } catch (JsonLineCountMisMatchException e) {
      log.error("JsonLineCountMisMatchException occurred {}", e.getMessage());
      processResponseService.sendErrorNotificationEmail(e, s3Url, snsMessageId, transmissionId);
      throw new JsonLineCountMisMatchException(e.getMessage());
    } catch (Exception e) {
      log.error("Unexpected error occurred {}", e.getMessage());
      processResponseService.sendErrorNotificationEmail(e, s3Url, snsMessageId, transmissionId);
      throw new RuntimeException(e);
    }
    return stepFunctionInput;
  }
}
