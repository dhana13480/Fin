package org.finra.rmcs.function;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.exception.BatchFileDmTriggerException;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;
import org.finra.rmcs.service.impl.BatchFileDmTriggerServiceImpl;
import org.finra.rmcs.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BatchFileDmTriggerLambda implements Function<SQSEvent, Map<String, Object>> {

  @Value("${stepfunction.arn}")
  private String stepFunctionARN;

  private final BatchFileDmTriggerServiceImpl batchFileDmTriggerServiceImpl;

  @Autowired
  public BatchFileDmTriggerLambda(BatchFileDmTriggerServiceImpl batchFileDmTriggerServiceImpl) {
    this.batchFileDmTriggerServiceImpl = batchFileDmTriggerServiceImpl;
  }

  /**
   * Applies this function to the given argument.
   *
   * @param sqsEvent the function argument
   * @return the function result
   */
  @Override
  public Map<String, Object> apply(SQSEvent sqsEvent) {
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
            .append(sqsEvent)
            .toString();
    log.info("{} message: method entry", methodName);
    log.info("batch file dm trigger lambda starts with sqs event json:\n{}", sqsEvent);
    Map<String, Object> returnMap = new HashMap<>();

    if(Util.isDryRun(sqsEvent)){
      log.info("dry run mode");
      returnMap.put(Constants.DRY_RUN, "success");
      return returnMap;
    }

    if (!Util.isSourceValid(sqsEvent)) {
      log.info("Invalid Message source");
      Util.populateReturnMap(
          Constants.COMPLETION_STATUS_SKIP,
          Constants.COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_SOURCE,
          returnMap);
      log.info(Constants.RETURN_MAP_VALUE_LOG, returnMap);
      return returnMap;
    }

    if (!Util.isMessageValid(sqsEvent)) {
      log.info("MessageAttributes not found in the SQS body or not a valid json");
      Util.populateReturnMap(
          Constants.COMPLETION_STATUS_SKIP,
          Constants.COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_ATTRIBUTES_NOT_FOUND,
          returnMap);
      log.info(Constants.RETURN_MAP_VALUE_LOG, returnMap);
      return returnMap;
    }

    String messageId = Util.getMessageId(sqsEvent);
    if (StringUtils.isBlank(messageId)) {
      log.info("SNS Message ID not found in the SQS body");
      Util.populateReturnMap(
          Constants.COMPLETION_STATUS_SKIP,
          Constants.COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_MESSAGE_ID_NOT_FOUND,
          returnMap);
      log.info(Constants.RETURN_MAP_VALUE_LOG, returnMap);
      return returnMap;
    }

    try {
      BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent =
          Util.getBusinessObjectDataStatusChangeEvent(sqsEvent);

      // Only processing DM event
      // when BusinessObjectDefinitionName matches REGEX RMCS-[A-Z]{4,5}-RECEIVABLES-IN$
      // and NewBusinessObjectDataStatus is VALID
      if (businessObjectDataStatusChangeEvent
              .getBusinessObjectDataKey()
              .getBusinessObjectDefinitionName()
              .matches(Constants.REGEX_BIZ_DEFINITION_NAME_KEY_WORD_RECEIVABLES_IN)
          && businessObjectDataStatusChangeEvent
              .getNewBusinessObjectDataStatus()
              .equalsIgnoreCase(Constants.VALID_BUSINESS_OBJECT_DATA_STATUS)) {
        String logMsg =
            String.format(
                "Received a valid DM event for %s version %s with the partition values %s %s",
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
        Map<String, Object> inputMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        inputMap.put(
            Constants.BIZ_OBJ_STATUS_CHANGE_EVENT,
            objectMapper.writeValueAsString(businessObjectDataStatusChangeEvent));
        inputMap.put(Constants.SNS_MESSAGE_ID_KEY, messageId);

        String inputStr = objectMapper.writeValueAsString(inputMap);
        log.info("Triggering Step function with input {}", inputStr);

        batchFileDmTriggerServiceImpl.triggerStepFunction(inputStr, stepFunctionARN);
        Util.populateReturnMap(
            Constants.COMPLETION_STATUS_SUCCESS,
            String.format(Constants.COMPLETION_DETAILS_VALID_DM_MESSAGE, logMsg),
            returnMap);
      } else {
        log.info("Not a valid RECEIVABLES-IN DM event, skipping");
        Util.populateReturnMap(
            Constants.COMPLETION_STATUS_SKIP,
            Constants.COMPLETION_DETAILS_SKIP_SQS_EVENT,
            returnMap);
      }
    } catch (Exception e) {
      log.error("Exception occurred {}", e.getMessage());
      // throw new Exception for the time being, it may be enhanced in the future with an email
      // notification.
      throw new BatchFileDmTriggerException(e.getMessage());
    }
    log.info(Constants.RETURN_MAP_VALUE_LOG, returnMap);
    return returnMap;
  }
}
