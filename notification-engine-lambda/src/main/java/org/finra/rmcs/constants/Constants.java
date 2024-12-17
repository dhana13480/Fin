package org.finra.rmcs.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
  public static final String CLASS = "class: ";
  public static final String METHOD = "method: ";
  public static final String CORRELATION_ID = "correlationId: ";
  public static final String TRANSMISSION_ID = "transmission Id : ";
  public static final String SNS_MESSAGEID = "SNS MessageId: ";
  public static final String SPACE = " ";
  public static final String SQS_MESSAGE_ATTRIBUTES = "MessageAttributes";
  public static final String SQS_TYPE = "Type";
  public static final String SQS_NOTIFICATION = "Notification";
  public static final String SNS_MESSAGE_ID = "MessageId";
  public static final String SQS_EVENT_MESSAGE = "Message";
  public static final String DRY_RUN = "dryRun";
  public static final String EVENT_SOURCE = "EVENT_SOURCE";

  public static final String SQS_EVENT_SOURCE= "aws:sqs";
  public static final String COMPLETION_STATUS = "COMPLETION_STATUS";
  public static final String COMPLETION_DETAILS = "DETAILS";
  public static final String COMPLETION_STATUS_SUCCESS = "SUCCESS";
  public static final String COMPLETION_STATUS_FAILED = "FAILED";
  public static final String COMPLETION_STATUS_SKIP = "SKIP";
  public static final String COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_MESSAGE_ID_NOT_FOUND =
      "Not a valid SNS message due to SNS Message ID not found";
  public static final String COMPLETION_DETAILS_NOT_VALID_SNS_MESSAGE_SOURCE =
      "Not a valid SNS message source";
  public static final String RETURN_MAP_VALUE_LOG = "return map value: {}";

  public static final String COMPLETION_SUCCESS_DETAILS =
      "Event Process Completed";
  public static final String COMPLETION_FAILED_DETAILS =
      "Event Process Failed";

  public static final Long RETRY_BACKOFF_PERIOD = 2000l;
  public static final int RETRY_MAX_ATTEMPT = 3;
  public static final String EMAIL = "email";
  public static final String AUDIENCE_TYPE = "EMAILS";
  public static final String BEARER = "Bearer ";
  public static final String RMCS = "RMCS";
  public static final String SPRING_PROFILES_ACTIVE = "SPRING_PROFILES_ACTIVE";
  public static final String ACTION_LINK = "https://finra.org";
  public static final String FINRA_HELP_DESK = "FINRA Help Desk";
  public static final String SUPPORT_CONTACT = "555-555-5555";
  public static final String NASDCORP = "nasdcorp";
  public static final String RESP_FORMATTED_DATE = "MM/dd/yyyy";
  public static final String yyyy_MM_dd = "yyyy-MM-dd";

  public static final String SUPP_START_DAY = "Monday";
  public static final String SUPP_END_DAY = "Friday";
  public static final String SUPP_START_TIME = "9AM";
  public static final String SUPP_END_TIME = "5PM";
  public static final String U4_REQ_GRP = "DEF Group";
  public static final String TEMPLATE_NAME = "RMCS_standardNotification_ebill";
  public static final String SUBSCRIPTION_GRP_NAME = "RMCS Notifications";
  public static final String SUBSCRIPTION_TYPE_NAME = "EBILL";
  public static final String SOURCE_APP_NAME = "Payments Service";
  public static final String NOW = "now";
  public static final String ANNOUNCEMENT = "ANNOUNCEMENT";
  public static final String PUBLISED = "PUBLISHED";
  public static final String ENCODING_JSON_VALUE = "application/json";
  public static final String GRANT_TYPE = "?grant_type=";
  public static final String EMPTY_STR = "";
  public static final String X_AUTH_KEY = "X-Auth-Key";
  public static final String AUTHORIZATION = "Authorization";
  public static final String ACI_PAYMENT_TOKEN_URL_ENCODED_VALUE =
          "application/x-www-form-urlencoded";
  public static final String BILLER_ID = "?billerId=";
  public static final String AGS = "RMCS";
  public static final String CONST_SEMI_COLON = ";";
  public static final String BREAK = "<br/>";
  public static final String ORG_FINRA_RMCS_REPO = "org.finra.rmcs.repo";
  public static final String RMCS_ENTITY_MANAGER_FACTORY = "rmcsEntityManagerFactory";
  public static final String RMCS_TRANSACTION_MANAGER = "rmcsTransactionManager";
  public static final String ORG_POSTGRESQL_DRIVER = "org.postgresql.Driver";
  public static final String ORG_FINRA_RMCS_ENTITY = "org.finra.rmcs.entity";
  public static final String ORG_FINRA_RMCS = "org.finra.rmcs";
  public static final String ORG_FINRA = "org.finra";

  // Hibernate Properties Start
  public static final String HIBERNATE_DIALECT = "hibernate.dialect";
  public static final String ORG_HIBERNATE_DIALECT_POSTGRESQL_DIALECT = "org.hibernate.dialect.PostgreSQLDialect";
  public static final String HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE = "hibernate.cache.use_second_level_cache";
  public static final String HIBERNATE_SHOW_SQL = "hibernate.show_sql";
  public static final String HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
  public static final String HIBERNATE_JDBC_BATCH_SIZE = "hibernate.jdbc.batch_size";
  public static final String HIBERNATE_ORDER_INSERTS = "hibernate.order_inserts";
  public static final String HIBERNATE_ENABLE_LAZY_LOAD_NO_TRANS = "hibernate.enable_lazy_load_no_trans";
  public static final String HIBERNATE_JDBC_LOB_NON_CONTEXTUAL_CREATION = "hibernate.jdbc.lob.non_contextual_creation";
  public static final String HIBERNATE_FLAG_FALSE = "false";
  public static final String HIBERNATE_FLAG_TRUE = "true";
  // Hibernate Properties End
  public static final String TABLE_DATA = "TABLE_DATA";
  public static final String AMOUNT = "transaction_amount";
  public static final String TRANSACTION_ID = "transaction_id";
  public static final String FROM_ORG_ID = "From_org_id";
  public static final String FROM_ORG_NAME = "From_org_name";
  public static final String TO_ORG_NAME = "To_org_name";
  public static final String TO_ORG_ID = "To_org_id";
  public static final String CUSTOMER_NAME = "CUSTOMERNAME";
  public static final String CUSTOMER_BALANCE = "CustomerBalance";
  public static final String NO_DATA_FOUND_FOR_AUTOPAY ="No data found for the user";

  public static final String AFFILIATE_FIRM_ORG_ID = "affiliate_firm_org_id is required";
  public static final String AFFILIATE_FIRM_NAME = "affiliate_firm_name is required";
  public static final String TRANSFER_AMOUNT = "transfer_amount is required";
  public static final String TOTAL_AMOUNT = "total_amount is required";
  public static final String AFFILIATE_FIRMS_ARE_REQUEST = "affiliate_firms are  request";
  public static final String  INVALID_REQUEST = "Invalid request";
  public static final String  CRDRG = "CRDRG";
  public static final String EBILL = "EBILL";
  public static final String AFFILIATED_FIRM_TRANSFER = "AffiliatedFirmTransfer";
  public static final String SUBMITTED = "Submitted";
  public static final String AFT = "AFT";
  public static final String AUTO_PAY_DELETE_SUCCESS = "AutoPay configuration deleted successfully";
  public static final String AUTO_PAY_DELETE_ERROR1 = "Unable to find the AutoPay";

  public static final String TRANSFER_AMOUNT_INVALID_INPUT = "invalid input for transfer_amount";
  public static final String TOTAL_AMOUNT_INVALID_INPUT = "invalid input for total_amount";
  public static final String ONLY_NUMBERS_ALLOWED = "[0-9]+[\\\\.]?[0-9]*";
  public static final String DECIMAL_FORMAT = "\"%.2f\"";

  public static final String MODULE_AFT = "AFT";
  public static final String MODULE_REFUND = "REFUND";


  public static final String REFUND_EMAIL_TEMPLATE_VARIABLE_KEY_DATE = "DATE";
  public static final String REFUND_EMAIL_TEMPLATE_VARIABLE_KEY_TRANSACTION_ID = "TRANSACTION_ID";
  public static final String REFUND_TRANSACTION_ID = "{Transaction_ID}";
  public static final String REFUND_EMAIL_TEMPLATE_VARIABLE_KEY_AMOUNT = "AMOUNT";
  public static final String REFUND_EMAIL_TEMPLATE_VARIABLE_KEY_TRANSACTION_STATUS =
      "TRANSACTION_STATUS";
  public static final String REFUND_EMAIL_TEMPLATE_VARIABLE_KEY_ORG_ID = "ORG_ID";
  public static final String REFUND_EMAIL_TEMPLATE_VARIABLE_KEY_CUSTOMER_NAME = "CUSTOMER_NAME";
  public static final String NON_PROD_REFUND_EMAIL_SUBJECT_REFUND_SUMBITTED =
      "QC FINRA E-Bill Refund Request {TRANSACTION_ID} Received";
  public static final String NOTIFICATION_ENGINE = "notification_engine";
  public static final String BUSINESS_UNIT_LONG_DESCRIPTION = "BUSINESSUNITLONGDESCRIPTION";
  public static final String THRESHOLD_AMOUNT = "THRESHOLDAMOUNT";
  public static final String ESTIMATED_BALANCE = "ESTIMATEDBALANCE";
  public static final String ORG_ID = "ORGID";
  public static final String BALANCE_DATE = "BALANCEDATE";

  public static final String AUTOPAY_BUS_UN_LONG_DS = "{{bus_un_long_ds}}";
  public static final String AUTOPAY_CRD = "{{crd}}";
  public static final String AUTOPAY_AMOUNT = "{{amount}}";
  public static final String AUTOPAY_TRANSACTION_ID = "{{transaction_id}}";
  public static final String AUTOPAY_FAILURE_REASON = "{{failure_reason}}";
  public static final String AUTOPAY_TRANSACTION_ID_REFERENCE = "The reference number for this transaction is {{transaction_id}}.</br>";

  public static final String NEW_INVOICE_AVAILABLE = "NEW_INVOICE_AVAILABLE";
  public static final String INVOICE_PAST_DUE = "INVOICE_PAST_DUE";
  public static final String LIST_OF_INVOICES = "LISTOFINVOICES";
  public static final String BUSINESSUNITLONGDESCRIPTION = "BUSINESSUNITLONGDESCRIPTION";
  public static final String DAYS_CONFIGURED = "DAYSCONFIGURED";


  public static final String ENABLER = "ENABLER";

  public static final String FAILURE_REASON = "Payment Failed due to Account validation failure";

  public static final String DATETIME_FORMAT_BILL_SUBMIT_TIME = "MM/dd/yyyy HH:mm";
  public static final String EMPTY_SPACE = " ";
  public static final String ET_TIME_ZONE = "Eastern Time";
  public static final String EST_TIME_ZONE = "America/New_York";
  public static final String UTC_TIME_ZONE = "UTC";
  public static final String DATE_FORMAT_BILL_SUBMIT_TIME = "MM/dd/yyyy";

  public static final String NON_PROD_CC_TEST_EMAIL = "veera.yenamadala@finra.org";
  public static final String NON_PROD_BCC_TEST_EMAIL = "DL-CorpSysQA@finra.org";
  public static final String BUYER_EMAIL = "BUYER_EMAIL";

  public static final String HTML ="<html>";
  public static final String BODY = "<body> ";
  public static final String TD = "</td>";
  public static final String HTMLTAG = "</table></body></html>";
  public static final String TABLE = "<table border=\"1\">";
  public static final String TRANSACTION_DATE_TABLE = "<th>Transaction Date</th>";
  public static final String TRANSACTION_ID_TABLE = "<th>Transaction ID</th>";
  public static final String HTML_STRING =  "html {}:";
  public static final String SUCCESS =  "Success";
}
