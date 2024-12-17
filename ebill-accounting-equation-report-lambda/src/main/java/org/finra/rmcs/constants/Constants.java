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
  public static final String CERT_PW = "changeit";
  public static final String AGS = "RMCS";
  public static final String NASDCORP = "nasdcorp";
  public static final String SESSION_USER_ID = "sessionUserId";
  public static final String SESSION_ORG_ID = "sessionOrgId";
  public static final String BUSINESS_UNIT_CRDRG = "CRDRG";
  public static final String EMPTY_SPACE = " ";
  public static final String FINRA = "FINRA";

  public static final String TEMP_FOLDER = "/tmp/";
  public static final String CSV_FILE_EXTENSION = ".csv";
  public static final String EST_TIME_ZONE = "America/New_York";
  public static final String UTC_TIME_ZONE = "UTC";
  public static final String DATE_FORMAT_BILL_SUBMIT_TIME = "MM/dd/yyyy";

  public static final String COMPLETED = "Completed";
  public static final String VALID = "Valid";

  public static final String LOG_FORMAT_WITH_EVENT = "app: {}, method: {}, correlationId: {},event: {}, message: {}";

  public static final String LOG_FORMAT = "app: {}, method: {}, transmissionId: {}, message: {}";

  public static final String ACC_EQUATION_FILE_NAME = "Accounting_Equation_Report";
  public static final String ACC_EQUATION_REPORT_SUBJECT = "Accounting Equation Check Errors";
  public static final String FF_ACC_EQUATION_REPORT_SHEET_NAME = "CRD";
  public static final String INVOICE_ACC_EQUATION_REPORT_SHEET_NAME = "INVOICE";
  public static final String[] FF_ACC_EQUATION_REPORT_OUT_BOUND_HEADERS = {
      "Revenue Stream", "Org ID", "Beginning Balance", "Ending Balance", "Calculated Ending Balance", "Variance"
  };
  public static final String[] INVOICE_ACC_EQUATION_REPORT_OUT_BOUND_HEADERS = {
      "Revenue Stream", "Org ID", "Customer ID", "Invoice ID", "Invoice Date", "Invoice Amount","Invoice Balance","Calculated Invoice Balance","Variance"
  };
  public static final String ACCOUNTING_EQUATION_REPORT = "AccountingEquationReport";

  public static final String XLSX_FILE_EXTENSION = ".xlsx";
  public static final String SPACE = " ";
  public static final String SQS_MESSAGE_ATTRIBUTES = "MessageAttributes";
  public static final String SQS_TYPE = "Type";
  public static final String SNS_MESSAGE_ID = "MessageId";
  public static final String SQS_NOTIFICATION = "Notification";
  public static final String RETURN_MAP_VALUE_LOG = "return map value: {}";
  public static final String COMPLETION_SUCCESS_DETAILS =
      "Event Process Completed";
  public static final String COMPLETION_STATUS_SUCCESS = "SUCCESS";
  public static final String COMPLETION_STATUS = "COMPLETION_STATUS";
  public static final String COMPLETION_DETAILS = "DETAILS";
  public static final String COMPLETION_STATUS_SKIP = "SKIP";
  public static final String COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_MESSAGE_ID_NOT_FOUND =
      "Not a valid SNS message due to SNS Message ID not found";
  public static final String SQS_EVENT_MESSAGE = "Message";
  public static final String COMPLETION_STATUS_FAILED = "FAILED";
  public static final String COMPLETION_FAILED_DETAILS =
      "Event Process Failed";

  public static final String LAMBDA_NAME = "RMCS-Ebill-Accounting-Equation-Report-Lambda";

  public static final String ACCOUNTING_EQUATION_REPORT_EMAIL_SENT_SUCCESS = "ACCOUNTING_EQUATION_REPORT_EMAIL_SENT_SUCCESS";
  public static final String ACCOUNTING_EQUATION_REPORT_EMAIL_SENT_FAILED = "ACCOUNTING_EQUATION_REPORT_EMAIL_SENT_FAILED";
  public static final String ACCOUNTING_EQUATION_REPORT_EXCEPTION = "ACCOUNTING_EQUATION_REPORT_EXCEPTION";

}
