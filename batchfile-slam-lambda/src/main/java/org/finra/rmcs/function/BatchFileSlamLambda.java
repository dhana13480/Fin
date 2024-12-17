package org.finra.rmcs.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;
import org.finra.rmcs.service.impl.BatchFileSlamServiceImpl;
import org.finra.rmcs.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BatchFileSlamLambda implements Function<Map<String, Object>, Map<String, Object>> {

  private final BatchFileSlamServiceImpl batchFileSlamService;

  @Autowired
  public BatchFileSlamLambda(BatchFileSlamServiceImpl batchFileSlamService) {
    this.batchFileSlamService = batchFileSlamService;
  }

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
            .append(Constants.SQS_EVENT_LOG)
            .append(input)
            .toString();
    log.info("{} message: method entry", methodName);
    log.info("batch file slam lambda starts with sqs event json:\n{}", input);

    boolean dryRun =
        Boolean.parseBoolean(input.getOrDefault(Constants.DRY_RUN, Constants.FALSE).toString());
    log.info("dryRun: {}", dryRun);
    if (dryRun) {
      log.info("dry run mode");
      input.put(Constants.DRY_RUN, Constants.SUCCESS);
      return input;
    }

    try {
      String slamId = input.getOrDefault(Constants.SLAM_ID, StringUtils.EMPTY).toString();
      String eventStep;
      String revenueStream;

      if (StringUtils.isBlank(slamId)) {
        eventStep = Constants.EVENT_STEP_START;
        String dmEvent = input.get(Constants.BIZ_OBJ_STATUS_CHANGE_EVENT).toString();
        BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent =
            new ObjectMapper().readValue(dmEvent, BusinessObjectDataStatusChangeEvent.class);
        revenueStream =
            Util.getRevenueStream(
                businessObjectDataStatusChangeEvent.getBusinessObjectDataKey().getNamespace());
        slamId = UUID.randomUUID().toString();
        input.put(Constants.SLAM_ID, slamId);
      } else {
        eventStep = Constants.EVENT_STEP_END;
        revenueStream = input.get(Constants.REVENUE_STREAM).toString();
      }

      String status = StringUtils.isNotBlank(revenueStream) ? Constants.SUCCESS : Constants.FAILURE;
      String event =
          Util.generateSlamEvent(
              Util.getWorkflowKey(revenueStream),
              slamId,
              eventStep,
              status,
              Util.getSlamDateTime());
      batchFileSlamService.sendSlamEvent(event);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return input;
  }
}
