package org.finra.rmcs.constants;

public class Constants {

  public static final String EXTERNAL_SESSION_NAME =
      "RMCS-Invoice-" + System.getenv().getOrDefault("SPRING_PROFILES_ACTIVE", "local");
  public static final int EXTERNAL_SESSION_DURATION = 3600;
  public static final String S3_FILENAME_PREFIX = "invoices_%s_";
  public static final String S3_FILENAME_EXTENSION = ".jsonl";
  public static final String DATETIME_FORMAT = "yyyyMMdd_HHmmss";
  public static final String S3_FILENAME_FORMAT = "%s%s%s";
  public static final String S3_KEY_FORMAT = "%s%s";
  public static final String FALSE = "false";
  public static final String TRUE = "true";
  public static final String CLASS = "class: ";
  public static final String METHOD = "method: ";
  public static final String CORRELATION_ID = "correlationId: ";

  public static final String TESTING = "testing";
  public static final String TESTINGPATH = "AUTOMATION/";

  public static final String REVENUE_STREAM = "revenue_stream";
  public static final String INPUT = "input: ";
  public static final String INTERNAL_BUCKET_KMS_KEY = "alias/appdata-kms";

  public static final String BLANK_STRING = "";
  public static final String RMCS = "RMCS";
  public static final String NASDCORP = "nasdcorp";
  public static final String ORG_FINRA_RMCS = "org.finra.rmcs";
  public static final String ORG_FINRA = "org.finra";
  public static final String ORG_FINRA_RMCS_REPO = "org.finra.rmcs.repo";
  public static final String RMCS_ENTITY_MANAGER_FACTORY = "rmcsEntityManagerFactory";
  public static final String RMCS_TRANSACTION_MANAGER = "rmcsTransactionManager";
  public static final String SPRING_PROFILES_ACTIVE = "SPRING_PROFILES_ACTIVE";
  public static final String ORG_POSTGRESQL_DRIVER = "org.postgresql.Driver";

  public static final String ORG_FINRA_RMCS_ENTITY = "org.finra.rmcs.entity";

  // Hibernate Properties Start
  public static final String HIBERNATE_DIALECT = "hibernate.dialect";
  public static final String ORG_HIBERNATE_DIALECT_POSTGRESQL_DIALECT =
      "org.hibernate.dialect.PostgreSQLDialect";
  public static final String HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE =
      "hibernate.cache.use_second_level_cache";
  public static final String HIBERNATE_SHOW_SQL = "hibernate.show_sql";
  public static final String HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
  public static final String HIBERNATE_JDBC_BATCH_SIZE = "hibernate.jdbc.batch_size";
  public static final String HIBERNATE_ORDER_INSERTS = "hibernate.order_inserts";
  public static final String HIBERNATE_ENABLE_LAZY_LOAD_NO_TRANS =
      "hibernate.enable_lazy_load_no_trans";
  public static final String HIBERNATE_JDBC_LOB_NON_CONTEXTUAL_CREATION =
      "hibernate.jdbc.lob.non_contextual_creation";
  // Hibernate Properties End

  public static final String PREFIX = "PREFIX";
  public static final String RMCS_AUTOMATION_TO_WORKDAY = "RMCS/AUTOMATION/to_workday/";
  public static final String OPERATION = "OPERATION";
  public static final String LIST = "LIST";
  public static final String LATEST = "LATEST";
  public static final String CLEANUP = "CLEANUP";
  public static final String COPY = "COPY";
  public static final String FILE_NAME_KEY = "FILENAMEKEY";
  public static final String FILE_NAMES = "FILENAMES";
  public static final String DATE = "date";
  public static final String SUCCESS = "Success";

  public static final String OBJECT_SIZE_AFTER_DELETE = "Objects size after delete - %s";
  public static final String ERROR_OCCURRED_CONVERTING_RECEIVABLE_DATA =
      "Error Occurred while converting receivable data into jsonline";
  public static final String JSON_LINES_ERROR =
      "Error while converting Receivable with ID: {}, {}, {}";
  public static final String STATS_JSON =
      "{\"Stats\": {\"Total Count\": %s ,\"Total Line Count\":0 ,\"Total Amount\": 0}}";

  public static final String CONTEXT = "context";
  public static final String PROD = "prod";
  public static final String TESTING_1 = "Testing";
  public static final String LOCAL_BUCKET = "LOCAL_BUCKET";
  public static final String LOCAL_PREFIX = "LOCAL_PREFIX";
  public static final String SLASH = "/";
  public static final String COMMA = ",";
  public static final String SPACE = " ";
  public static final String UNEXPECTED_VALUE = "Unexpected value: ";

  public static final String LAMBDA_NAME = "send-invoice-to-wd-lambda";
  public static final String EVENT_SENT_TO_WD_RECEIVABLE = "SENT_TO_WD_RECEIVABLE";
  public static final String EVENT_ERROR_SENT_TO_WD_RECEIVABLE = "ERROR_SENT_TO_WD_RECEIVABLE";
  public static final String REGULAR_EVENT_LOG_FORMAT =
      "app: {}, methodName: {}, event: {}, transmissionId: {}, invoiceId: {}, revenueStream: {}";

  public static final String DRY_RUN = "dryRun";

  public static final String PAYMENT_RECEIVED_FLAG = "Y";

  private Constants() {
    throw new IllegalStateException(" all are static methods ");
  }
}
