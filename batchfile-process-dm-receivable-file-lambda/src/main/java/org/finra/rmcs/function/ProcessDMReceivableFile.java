package org.finra.rmcs.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finra.herd.sdk.model.BusinessObjectData;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;
import org.finra.rmcs.service.impl.HerdServiceImpl;
import org.finra.rmcs.service.impl.ProcessDmReceivableFileServiceImpl;
import org.finra.rmcs.util.Util;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ProcessDMReceivableFile implements Function<Map<String, Object>, Map<String, Object>> {

  private final ProcessDmReceivableFileServiceImpl processDmReceivableFileService;

  private final HerdServiceImpl herdService;

  @Override
  public Map<String, Object> apply(Map<String, Object> input) {
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
            .append(input)
            .toString();
    log.info("{} message: method entry", methodName);

    boolean dryRun = Boolean
        .parseBoolean(input.getOrDefault(Constants.DRY_RUN, Constants.FALSE).toString());
    log.info("dryRun: {}", dryRun);
    if (dryRun) {
      log.info("dry run mode");
      input.put(Constants.DRY_RUN, "success");
      return input;
    }

    String dmEvent = input.get(Constants.BIZ_OBJ_STATUS_CHANGE_EVENT).toString();
    input.remove(Constants.BIZ_OBJ_STATUS_CHANGE_EVENT);
    String s3Url = null;
    String transmissionId = null;
    String revenueStream = null;
    BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent = null;
    log.info("dm-receivable-file lambda begins with the valid dm event: {}", dmEvent);

    try {
      businessObjectDataStatusChangeEvent =
          new ObjectMapper().readValue(dmEvent, BusinessObjectDataStatusChangeEvent.class);
      String logMsg =
          String.format(
              "Start processing a valid DM event for %s %s with the partition values %s %s",
              businessObjectDataStatusChangeEvent
                  .getBusinessObjectDataKey()
                  .getBusinessObjectDefinitionName(),
              businessObjectDataStatusChangeEvent
                  .getBusinessObjectDataKey()
                  .getBusinessObjectDataVersion(),
              businessObjectDataStatusChangeEvent.getBusinessObjectDataKey().getPartitionValue(),
              businessObjectDataStatusChangeEvent
                  .getBusinessObjectDataKey()
                  .getSubPartitionValues()
                  .get(0));
      log.info(logMsg);
      BusinessObjectData businessObjectData =
          herdService.getBusinessObjectData(
              businessObjectDataStatusChangeEvent.getBusinessObjectDataKey());
      s3Url = Util.generateS3Location(businessObjectData);
      log.info("Generated S3 Url: {}", s3Url);
      transmissionId = Util.generateTransmissionId(businessObjectData);
      log.info("Generated transmissionId: {}", transmissionId);
      revenueStream = Util.getRevenueStreamFromNameSpace(businessObjectDataStatusChangeEvent);
      log.info("Revenue Stream: {}", revenueStream);
      log.info(
          Constants.EVENT_FILE_RECEIVED_LOG_FORMAT,
          Constants.LAMBDA_NAME,
          methodName,
          Constants.EVENT_FILE_RECEIVED,
          Constants.SOURCE_FILE_UPLOAD,
          transmissionId,
          revenueStream,
          s3Url);

      input.put(Constants.FILE_URL_KEY, s3Url);
      input.put(Constants.TRANSMISSION_ID_KEY, transmissionId);
      input.put(Constants.REVENUE_STREAM_KEY, revenueStream);

      processDmReceivableFileService.upsertEntry(
          transmissionId, s3Url, revenueStream, businessObjectDataStatusChangeEvent, input);

      processDmReceivableFileService.validateReceivableFile(
          transmissionId, s3Url, revenueStream, businessObjectData, input);

    } catch (Exception e) {
      log.error("Unexpected error: {}", e.getMessage());
      processDmReceivableFileService.handleUnRetryableException(
          transmissionId, s3Url, revenueStream, businessObjectDataStatusChangeEvent, input);
      return input;
    }

    return input;
  }
}
