package org.finra.rmcs.constants;

public class Constants {

  public static final String FALSE = "false";
  public static final String TRUE = "true";
  public static final String CLASS = "class: ";
  public static final String METHOD = "method: ";
  public static final String CORRELATION_ID = "correlationId: ";
  public static final String AGS = "RMCS";
  public static final String SPRING_PROFILES_ACTIVE = "SPRING_PROFILES_ACTIVE";
  public static final String RMCS_BUSINESS_OBJECT_FORMAT_FILE_TYPE = "JSONL";
  public static final String RMCS_BUSINESS_OBJECT_USAGE = "BILLING";
  public static final String RMCS_BUSINESS_DEFINATION_NAME_OK = "RMCS-%S-RECEIVABLES-OUT-OK";
  public static final String RMCS_BUSINESS_DEFINATION_NAME_FAIL = "RMCS-%S-RECEIVABLES-OUT-FAIL";
  public static final String RMCS_BUSINESS_DEFINATION_NAME_FATAL = "RMCS-%S-RECEIVABLES-OUT-FATAL";
  public static final String RMCS_HERD_STORAGE = "RMCS_S3_MANAGED";
  public static final String ATTR_BUCKET_NAME = "bucket.name";
  public static final String ATTR_KMS_KEY_ID = "kms.key.id";
  public static final String DM_STATUS_VALID = "VALID";
  public static final String DM_STATUS_UPLOADING = "UPLOADING";
  public static final String DM_STATUS_INVALID = "INVALID";
  public static final String TRANSMISSION_ID_KEY = "transmission_id";
  public static final String TYPE_KEY = "type";

  public static final String STATUS_FATAL = "FATAL";
  public static final String STATUS_COMPLETED = "COMPLETED";
  public static final String LAMBDA_NAME = "PROCESS_RESPONSE_LAMBDA";
  public static final String META_DATA_KEY_JSON_LINE_COUNT = "json_line_count";
  public static final String FILE_URL_KEY = "file_url";
  public static final String SNS_MESSAGE_ID_KEY = "sns_message_id";
  public static final String NEW_LINE = "\n";

  public static final String INPUT = "input: ";

  public static final String DRY_RUN = "dryRun";

  private Constants() {
    throw new IllegalStateException(" all are static methods ");
  }
}
