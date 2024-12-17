package org.finra.rmcs.constants;

public class Constants {

  public static final String APPLICATION_NAME = "RMCS";
  public static final String ACTIVE_PROFILE = "SPRING_PROFILES_ACTIVE";
  public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
  public static final String ENTITY_PACKAGE = "org.finra.rmcs.entity";
  public static final String UPDATED_BY = "InvoiceReceivableService";
  public static final String FALSE = "false";
  public static final String TRUE = "true";
  public static final String CLASS = "class: ";
  public static final String METHOD = "method: ";
  public static final String CORRELATION_ID = "correlationId: ";
  public static final String DRY_RUN = "dryRun";

  public static final String INVOICE_STATUS = "INV";
  public static final String LAMBDA_NAME = "receivable-to-invoice-lambda";
  public static final String EVENT_INVOICED_RECEIVABLE = "INVOICED_RECEIVABLE";
  public static final String EVENT_ERROR_INVOICED_RECEIVABLE = "ERROR_INVOICED_RECEIVABLE";
  public static final String REGULAR_EVENT_LOG_FORMAT =
      "app: {}, methodName: {}, event: {}, transmissionId: {}, invoiceId: {}, revenueStream: {}";

  public static final String TO_DATE = "TO_DATE";
  public static final String FROM_DATE = "FROM_DATE";

  public static final String TIME_ZONE_AMERICA_NEW_YORK = "America/New_York";

  private Constants() {
    throw new IllegalStateException(" all are static methods ");
  }
}
