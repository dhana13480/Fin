package org.finra.rmcs.constants;

public class Constants {

  public static final String FALSE = "false";
  public static final String TRUE = "true";
  public static final String CLASS = "class: ";
  public static final String METHOD = "method: ";
  public static final String CORRELATION_ID = "correlationId: ";
  public static final String AGS = "RMCS";
  public static final String NASDCORP = "nasdcorp";
  public static final String SPRING_PROFILES_ACTIVE = "SPRING_PROFILES_ACTIVE";
  public static final String BIZ_OBJ_STATUS_CHANGE_EVENT = "biz_obj_status_change_event";
  public static final String SNS_MESSAGE_ID = "sns_message_id";
  public static final String ATTR_BUCKET_NAME = "bucket.name";
  public static final long FILE_SIZE_2_GB = 2147483648L;
  public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
  public static final String STATUS_FATAL = "FATAL";
  public static final String LAMBDA_NAME = "DM_RECEIVABLE_FILE_LAMBDA";
  public static final String EVENT_FILE_RECEIVED = "FILE_RECEIVED";
  public static final String EVENT_FILE_VALIDATION_SUCCESS = "FILE_VALIDATION_SUCCESS";
  public static final String EVENT_FILE_VALIDATION_FAILURE = "FILE_VALIDATION_FAILURE";
  public static final String EVENT_FILE_RECEIVED_LOG_FORMAT = "app: {}, methodName: {}, event: {}, source: {}, transmissionId: {}, revenueStream: {}, s3Url: {}";
  public static final String EVENT_FILE_VALIDATION_SUCCESS_LOG_FORMAT = "app: {}, methodName: {}, event: {}, source: {}, transmissionId: {}, revenueStream: {}, s3Url: {}, lineCount: {}";
  public static final String EVENT_FILE_VALIDATION_FAILURE_LOG_FORMAT = "app: {}, methodName: {}, event: {}, source: {}, transmissionId: {}, revenueStream: {}, s3Url: {}, lineCount: {}, error: {}";

  public static final String SOURCE_FILE_UPLOAD = "FILE_UPLOAD";
  public static final String TIME_ZONE_AMERICA_NEW_YORK = "America/New_York";
  public static final String DATE_TIME_FORMAT = "MM/dd/yyyy hh:mma Z";
  public static final String META_DATA_IS_NOT_MATCH =
      "Meta data is not matching with file data - %s";
  public static final String ERROR_PARSING_JSON =
      "Error parsing Json at index - %s";
  public static final String META_DATA_KEY_JSON_LINE_COUNT = "json_line_count";
  public static final String META_DATA_KEY_REVENUE_STREAM = "revenue_stream";
  public static final String UNKNOWN_VALUE = "UNKNOWN_VALUE";
  public static final String INPUT = "input: ";
  public static final String DATA_RETRIEVAL_PROTOCOL = "s3://";

  public static final String ERROR_CODE_RMCS_012 = "RMCS-012";
  public static final String ERROR_CODE_RMCS_010 = "RMCS-010";
  public static final String FATAL = "fatal";
  public static final String COMPLETED = "completed";
  public static final String TYPE = "type";
  public static final String RE_RUN_KEY = "re_run";
  public static final String TRANSMISSION_ID_KEY = "transmission_id";
  public static final String REVENUE_STREAM_KEY = "revenue_stream";
  public static final String FILE_URL_KEY = "file_url";

  public static final String DRY_RUN = "dryRun";

  private Constants() {
    throw new IllegalStateException(" all are static methods ");
  }
}
