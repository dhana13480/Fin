package org.finra.rmcs.constants;

public class Constants {

  public static final String AGS = "RMCS";
  public static final String SPRING_PROFILES_ACTIVE = "SPRING_PROFILES_ACTIVE";
  public static final String AUTHORIZATION = "Authorization";
  public static final String BEARER = "Bearer";
  public static final String CLASS = "class: ";
  public static final String METHOD = "method: ";
  public static final String CORRELATION_ID = "correlationId: ";
  public static final String INPUT = "input: ";
  public static final String FALSE = "false";
  public static final String TRUE = "true";

  public static final String FILE_URL = "file_url";
  public static final String TRANSMISSION_ID = "transmission_id";
  public static final String SNS_MESSAGE_ID = "sns_message_id";
  public static final String RECEIVABLE = "receivable";
  public static final String FILE_UPLOAD = "FILE_UPLOAD";
  public static final String TYPE = "type";
  public static final String COMPLETED = "COMPLETED";
  public static final String ERROR = "ERROR";
  public static final String LAMBDA_NAME = "PROCESS_RECEIVABLE_LAMBDA";

  public static final String DRY_RUN = "dryRun";

  private Constants() {
    throw new IllegalStateException(" all are static methods ");
  }
}
