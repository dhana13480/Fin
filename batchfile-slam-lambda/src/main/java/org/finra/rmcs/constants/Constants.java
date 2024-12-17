package org.finra.rmcs.constants;

import static org.finra.rmcs.common.constants.RevenueStreamEnum.ADFRF;
import static org.finra.rmcs.common.constants.RevenueStreamEnum.APIBI;
import static org.finra.rmcs.common.constants.RevenueStreamEnum.ATRBI;
import static org.finra.rmcs.common.constants.RevenueStreamEnum.CRDRG;
import static org.finra.rmcs.common.constants.RevenueStreamEnum.CRDRN;
import static org.finra.rmcs.common.constants.RevenueStreamEnum.ORFBI;
import static org.finra.rmcs.common.constants.RevenueStreamEnum.RGFEE;
import static org.finra.rmcs.common.constants.RevenueStreamEnum.TRACE;

import java.util.List;

public class Constants {
  public static final String CLASS = "class: ";
  public static final String METHOD = "method: ";
  public static final String CORRELATION_ID = "correlationId: ";
  public static final String SQS_EVENT_LOG = "sqsEvent is {}";
  public static final String FALSE = "false";
  public static final String RETURN_MAP_VALUE_LOG = "return map value -->{}";
  public static final String COMPLETION_STATUS = "COMPLETION_STATUS";
  public static final String COMPLETION_DETAILS = "DETAILS";
  public static final String BIZ_OBJ_STATUS_CHANGE_EVENT = "biz_obj_status_change_event";
  public static final String SLAM_ID = "slam_id";
  public static final String REVENUE_STREAM = "revenue_stream";
  public static final String DRY_RUN = "dryRun";
  public static final String SUCCESS = "success";
  public static final String FAILURE = "failure";
  public static final String EVENT_STEP_START = "start";
  public static final String EVENT_STEP_END = "end";
  public static final List<String> onboardedRS =
      List.of(
          APIBI.toString(),
          TRACE.toString(),
          ORFBI.toString(),
          RGFEE.toString(),
          ADFRF.toString(),
          CRDRG.toString(),
          CRDRN.toString(),
          ATRBI.toString());

  private Constants() {
    throw new IllegalStateException(" all are static methods ");
  }
}
