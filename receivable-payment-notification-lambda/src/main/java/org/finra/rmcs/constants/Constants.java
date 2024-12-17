package org.finra.rmcs.constants;

public class Constants {
  public static final String CLASS = "class: ";
  public static final String METHOD = "method: ";
  public static final String CORRELATION_ID = "correlationId: ";
  public static final String TRANSMISSION_ID = "transmission Id : ";
  public static final String SNS_MESSAGEID = "SNS MessageId: ";
  public static final String RETURN_MAP_VALUE_LOG = "return map value -->{}";
  public static final String COMPLETION_STATUS = "COMPLETION_STATUS";
  public static final String COMPLETION_DETAILS = "DETAILS";
  public static final String COMPLETION_STATUS_SUCCESS = "SUCCESS";
  public static final String COMPLETION_STATUS_SKIP = "SKIP";
  public static final String COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_MESSAGE_ID_NOT_FOUND =
      "Not a valid SNS message due to SNS Message ID not found";
  public static final String COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_SOURCE =
      "Not a valid SNS message source";
  public static final String COMPLETION_DETAILS_VALID_DM_MESSAGE =
      "Received a valid DM message, And Update to Receivable is Successful";
  public static final String SQS_MESSAGE_ATTRIBUTES = "MessageAttributes";
  public static final String SQS_TYPE = "Type";
  public static final String SQS_NOTIFICATION = "Notification";
  public static final String SNS_MESSAGE_ID = "MessageId";
  public static final String SQS_EVENT_MESSAGE = "Message";
  public static final String DRY_RUN = "dryRun";
  public static final String EVENT_SOURCE = "EVENT_SOURCE";
  public static final String SQS_EVENT_SOURCE= "aws:sqs";
  public static final String ORG_FINRA_RMCS = "org.finra.rmcs";
  public static final String ORG_FINRA = "org.finra";
  public static final String ORG_FINRA_RMCS_REPO = "org.finra.rmcs.repo";
  public static final String RMCS_ENTITY_MANAGER_FACTORY = "rmcsEntityManagerFactory";
  public static final String RMCS_TRANSACTION_MANAGER = "rmcsTransactionManager";
  public static final String SPRING_PROFILES_ACTIVE = "SPRING_PROFILES_ACTIVE";
  public static final String ORG_POSTGRESQL_DRIVER = "org.postgresql.Driver";
  public static final String PAYMENT_STATUS_PAID = "PAID";
  public static final String PAYMENT_RECEIVED_Y = "Y";

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
  public static final String FALSE = "false";
  public static final String TRUE = "true";
  public static final String RMCS = "RMCS";
  public static final String SPACE = " ";

  public static final String PAYMENT_STATUS_EXPIRED = "EXPIRED";
  public static final String CPACT = "CPACT";

  private Constants() {
    throw new IllegalStateException(" all are static methods ");
  }
}
