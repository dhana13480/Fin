package org.finra.rmcs.constants;

public class Constants {
  public static final String CLASS = "class: ";
  public static final String METHOD = "method: ";
  public static final String CORRELATION_ID = "correlationId: ";
  public static final String SQS_EVENT_LOG = "sqsEvent is {}";
  public static final String RETURN_MAP_VALUE_LOG = "return map value -->{}";
  public static final String COMPLETION_STATUS = "COMPLETION_STATUS";
  public static final String COMPLETION_DETAILS = "DETAILS";
  public static final String COMPLETION_STATUS_SUCCESS = "SUCCESS";
  public static final String COMPLETION_STATUS_FAIL = "FAIL";
  public static final String COMPLETION_STATUS_SKIP = "SKIP";
  public static final String COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_MESSAGE_ID_NOT_FOUND =
      "Not a valid SNS message due to SNS Message ID not found";
  public static final String COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_ATTRIBUTES_NOT_FOUND =
      "Not a valid SNS message due to MessageAttributes not found or not a valid json";
  public static final String COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_SOURCE =
      "Not a valid SNS message source";
  public static final String COMPLETION_DETAILS_SKIP_SQS_EVENT =
      "Not a valid DM Message, Skipped this SQS event";
  public static final String COMPLETION_DETAILS_VALID_DM_MESSAGE =
      "Received a valid DM message %s, And passed it to Step Function successfully";
  public static final String SQS_MESSAGE_ATTRIBUTES = "MessageAttributes";
  public static final String SQS_TYPE = "Type";
  public static final String SQS_NOTIFICATION = "Notification";
  public static final String SNS_MESSAGE_ID = "MessageId";
  public static final String SQS_EVENT_MESSAGE = "Message";
  public static final String REGEX_BIZ_DEFINITION_NAME_KEY_WORD_RECEIVABLES_IN =
      "RMCS-[A-Z]{4,5}-RECEIVABLES-IN$";
  public static final String VALID_BUSINESS_OBJECT_DATA_STATUS = "VALID";
  public static final String BIZ_OBJ_STATUS_CHANGE_EVENT = "biz_obj_status_change_event";
  public static final String SNS_MESSAGE_ID_KEY = "sns_message_id";

  public static final String DRY_RUN = "dryRun";
  public static final String EVENT_SOURCE = "EVENT_SOURCE";
  public static final String SQS_EVENT_SOURCE= "aws:sqs";

  private Constants() {
    throw new IllegalStateException(" all are static methods ");
  }
}
