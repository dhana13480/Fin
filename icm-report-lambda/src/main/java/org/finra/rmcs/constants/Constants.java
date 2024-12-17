package org.finra.rmcs.constants;

import java.util.List;

public class Constants {

  public static final String FALSE = "false";
  public static final String TRUE = "true";
  public static final String CLASS = "class: ";
  public static final String METHOD = "method: ";
  public static final String CORRELATION_ID = "correlationId: ";
  public static final String AGS = "RMCS";
  public static final String SPRING_PROFILES_ACTIVE = "SPRING_PROFILES_ACTIVE";
  public static final String DRY_RUN = "dryRun";
  public static final String SUCCESS = "success";

  public static final String REPORT_DATE = "reportDate";

  // if a revenue_stream payment_validation is false and also using OPAY or Ebill
  // then we need to add it to the EXCEPTION_LIST to avoid include its entry multiple times in ICM report
  // "CPACT", "MTRCS" for OPAY Path
  // "REGT", "TAFBI", "ADVRG", "APIBI", "MREGN" for EBill Path
  public static final List<String> EXCEPTION_LIST =
      List.of("CPACT", "MTRCS", "REGT", "TAFBI", "ADVRG", "APIBI", "MREGN");

  private Constants() {
    throw new IllegalStateException(" all are static methods ");
  }
}
