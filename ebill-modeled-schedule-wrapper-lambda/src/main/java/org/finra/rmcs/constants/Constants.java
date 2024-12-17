package org.finra.rmcs.constants;

public class Constants {
  public static final String RMCS = "RMCS";
  public static final String SPRING_PROFILES_ACTIVE = "SPRING_PROFILES_ACTIVE";
  public static final String CLASS = "class: ";
  public static final String METHOD = "method: ";
  public static final String CORRELATION_ID = "correlationId: ";
  public static final String INPUT = "input: ";
  public static final String DRY_RUN = "dryRun";
  public static final String FALSE = "false";
  public static final String TRUE = "true";
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
  public static final String SERVICE_NAME = "SERVICE_NAME";
  public static final String SUCCESS = "SUCCESS";
  public static final String FAILED = "FAILED";
  public static final String AUTOPAY = "AutoPay";
  public static final String PENDING_PAYMENTS_REALLOCATION = "PendingPaymentsReallocation";
  public static final String ALERTS_BATCH_PROCESS_SERVICE = "AlertsBatchProcess";
  public static final String AFT_QUEUED_TRANSACTIONS = "AftQueuedTransactions";
  public static final String AFT_MODULE = "AFT";
  public static final String STATUS_SUBMITTED = "SUBMITTED";
  public static final String INVALID_SERVICE_NAME = "invalid service name";

  public static final String AUTOPAY_BATCH_PROCESS = "AUTOPAY_BATCH_PROCESS";
  public static final String EBILL_REVENUE_STREAM = "CRDRG";
  public static final String AUTOPAY_PAYMENT_STATUS_PAID = "PAID";

  public static final String URL_PATH_VARIABLE_KEY_ORG_ID = "org_id";
  public static final String URL_PATH_VARIABLE_KEY_BU_ID = "bu_id";
  public static final String CONTENT_TYPE = "Content-Type";
  public static final String APPLICATION_JSON = "application/json";
  public static final String BILLER_ID_QRY_STR = "?billerId=";
  public static final String CERT_PW = "changeit";
  public static final String AGS = "RMCS";
  public static final String NASDCORP = "nasdcorp";
  public static final String FF_ALERTS_EMAIL = "FFAlertEmails";
  public static final String SESSION_USER_ID = "sessionUserId";
  public static final String SESSION_ORG_ID = "sessionOrgId";
  public static final String BUSINESS_UNIT_CRDRG = "CRDRG";
  public static final String AUTOPAY_FLAG_Y = "Y";
  public static final String AUTOPAY_PAYMENT_SUCCESSFULLY = "Payment processed successfully";
  public static final String ORG_CUST_STATUS_ACTIVE = "Active";
  public static final String AUTOPAY_TRANSACTION_TYPE = "AUTOPAY";
  public static final String AUTOPAY_PAYMENT_TYPE = "Ach";

  public static final String REFUND_PAYMENT_TYPE_ID = "4";
  public static final String REFUND_PAYMENT_STATE_ID = "7";
  public static final String REFUND_EMAIL = "REFUND_EMAIL";
  public static final String REFUND_EMAIL_DATE_FORMAT = "MM-dd-yyyy";
  public static final String REFUND_EMAIL_SUBJECT = "Flex-Funding_Refund_spreadsheet_";
  public static final String REFUND_EMAIL_BODY = "Number of refund requests in this email:";
  public static final String REFUND_PAYMENT_METHOD = "OUTSOURCED_CHECK";
  public static final String EMPTY_SPACE = " ";
  public static final String FINRA = "FINRA";
  public static final String[] REFUND_EMAIL_HEADERS = {
          "Account", "OrgId", "Refund Amount", "E-Bill Transaction ID", "Column 5 ", "Column 6", "Refund Request Report Generation Date",
           "Refund Method", "Column 9","Column 10","User ID","User Name","Org Name","Contact Email","Contact Number","Refund Request Date",
          "Description"
  };
  public static final String TEMP_FOLDER = "/tmp/";
  public static final String CSV_FILE_EXTENSION = ".csv";
  public static final String EST_TIMEZONE = "America/New_York";
  public static final String REFUND_EMAIL_FILE_NAME = "CRDRG_Refund_Detail_spreadsheet_";
  public static final String AUTOPAY_PAYMENT_FAILURE_MESSAGE = "Payment Failed due to Account validation failure";

  public static final String DATA_REFRESH_LOG = "DataRefreshLog";
  public static final String RUN_DATA_REFRESH = "RunDataRefresh";
  public static final String COMPLETED = "Completed";
  public static final String VALID = "Valid";

  public static final String SELECTED_ORG_ID = "?selected_org_id=";

}
