package org.finra.rmcs.constants;

public class Constants {
  public static final String RMCS = "RMCS";
  public static final String AGS = "RMCS";
  public static final String NASDCORP = "nasdcorp";

  public static final String CERT_PW = "changeit";
  public static final String LAMBDA_SERVICE_NAME = "EBILL_FINAPPS_NOTIFICATION_PROCESS_LAMBDA";
  public static final String SPRING_PROFILES_ACTIVE = "SPRING_PROFILES_ACTIVE";
  public static final String CLASS = "class: ";
  public static final String METHOD = "method: ";
  public static final String CORRELATION_ID = "correlationId: ";
  public static final String DRY_RUN = "dryRun";
  public static final String SPACE = " ";
  public static final String ORG_FINRA_RMCS = "org.finra.rmcs";
  public static final String ORG_FINRA = "org.finra";
  public static final String ORG_FINRA_RMCS_REPO = "org.finra.rmcs.repo";
  public static final String RMCS_ENTITY_MANAGER_FACTORY = "rmcsEntityManagerFactory";
  public static final String RMCS_TRANSACTION_MANAGER = "rmcsTransactionManager";
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
  public static final String HIBERNATE_FLAG_FALSE = "false";
  public static final String HIBERNATE_FLAG_TRUE = "true";
  // Hibernate Properties End

  public static final String SQS_EVENT_MESSAGE = "Message";

  public static final String COMPLETION_STATUS = "COMPLETION_STATUS";
  public static final String COMPLETION_DETAILS = "DETAILS";
  public static final String COMPLETION_STATUS_SUCCESS = "SUCCESS";
  public static final String COMPLETION_STATUS_FAILED = "FAILED";
  public static final String RETURN_MAP_VALUE_LOG = "return map value: {}";

  public static final String COMPLETION_SUCCESS_DETAILS =
      "Event Process Completed";
  public static final String COMPLETION_FAILED_DETAILS =
      "Event Process Failed";
  public static final String VALID_BUSINESS_OBJECT_STATUS = "VALID";
  public static final String HEALTH_PAYLOAD = "health";
  public static final String TEXT_ONLY_PATTERN = "[^a-zA-Z]";
  public static final String SQS_MESSAGE = "Message";
  public static final String DASH = "-";
  public static final String CSV = "csv";
  public static final String DOT = ".";
  public static final String FILE_NAME = "fileName";
  public static final String FILE_PATH = "filePath";
}
