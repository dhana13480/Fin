package org.finra.rmcs.constants;

public class Constants {
  public static final String CLASS = "class: ";
  public static final String METHOD = "method: ";
  public static final String BODY_JSON = "body-json";
  public static final String CORRELATION_ID = "correlationId: ";
  public static final String NASDCORP = "nasdcorp";
  public static final String ORG_FINRA_RMCS = "org.finra.rmcs";
  public static final String ORG_FINRA = "org.finra";
  public static final String ORG_FINRA_RMCS_REPO = "org.finra.rmcs.repo";
  public static final String RMCS_ENTITY_MANAGER_FACTORY = "rmcsEntityManagerFactory";
  public static final String RMCS_TRANSACTION_MANAGER = "rmcsTransactionManager";
  public static final String SPRING_PROFILES_ACTIVE = "SPRING_PROFILES_ACTIVE";
  public static final String ORG_POSTGRESQL_DRIVER = "org.postgresql.Driver";
  public static final String RMCS = "RMCS";
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
  public static final String TOKEN_CONTEXT = "context";
  public static final String FIP_JWKS_ENDPOINT = "FIP_JWKS_ENDPOINT";
  public static final String EXPIRED_TOKEN_MSG = "Expired token!";
  public static final String AGS = "RMCS";
  public static final String FALSE = "false";
  public static final String TRUE = "true";
  public static final String DATETIME_WITHOUT_MILLISECONDS_FORMAT = "uuuu-MM-dd'T'HH:mm:ss";
  public static final String DRY_RUN = "dryRun";
  public static final String DRY_RUN_PROCESSED_SUCCESSFULLY = "dryRun is success";
  public static final String MESSAGE_NODE = "message";

  private Constants() {
    throw new IllegalStateException(" all are static methods ");
  }
}
