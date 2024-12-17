package org.finra.rmcs.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.exception.SlamWorkflowKeyNotFoundException;

@Slf4j
public class Util {

  public static String getWorkflowKey(String revenueStream) {
    revenueStream = revenueStream.trim().toUpperCase();
    if (!Constants.onboardedRS.contains(revenueStream)) {
      throw new SlamWorkflowKeyNotFoundException(
          String.format(
              "Cannot determine SLAM workflow key, Please onboard %s workflow to SLAM",
              revenueStream));
    }
    return String.format("RMCS-RECEIVABLES-BATCH-PROCESS-%s-WORKFLOW", revenueStream);
  }

  @SneakyThrows
  public static String generateSlamEvent(
      String workflowKey, String slamId, String eventStep, String status, String timeStamp) {
    Map<String, Object> slamEvent = new HashMap<>();
    slamEvent.put("workflowKey", workflowKey);
    slamEvent.put("slamId", slamId);
    slamEvent.put("step", eventStep);
    slamEvent.put("status", status);
    slamEvent.put("timestampUTC", timeStamp);
    return new ObjectMapper().writeValueAsString(slamEvent);
  }

  public static String getSlamDateTime() {
    DateFormat slamDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    slamDate.setTimeZone(TimeZone.getTimeZone("UTC"));
    return slamDate.format(new Date());
  }

  public static String getRevenueStream(String nameSpace) {
    return StringUtils.substringAfterLast(nameSpace, "-");
  }
}
