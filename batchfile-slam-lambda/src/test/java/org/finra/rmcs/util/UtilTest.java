package org.finra.rmcs.util;

import org.finra.rmcs.exception.SlamWorkflowKeyNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class UtilTest {

  @Test
  public void testGetWorkflowKey() {
    String expected = "RMCS-RECEIVABLES-BATCH-PROCESS-APIBI-WORKFLOW";
    Assertions.assertEquals(expected, Util.getWorkflowKey("APIBI"));
  }

  @Test
  public void testGetWorkflowKey_whenException() {
    Throwable exception =
        Assertions.assertThrows(
            SlamWorkflowKeyNotFoundException.class, () -> Util.getWorkflowKey("Test"));
    Assertions.assertEquals(
        "Cannot determine SLAM workflow key, Please onboard TEST workflow to SLAM",
        exception.getMessage());
  }

  @Test
  public void testGenerateSlamEvent() {
    String actual =
        Util.generateSlamEvent(
            "TEST-WORKFLOW", "TEST-SLAM-ID", "TEST-EVENT-STEP", "TEST-STATUS", "TEST-UTC");
    Assertions.assertEquals(
        "{\"workflowKey\":\"TEST-WORKFLOW\",\"step\":\"TEST-EVENT-STEP\",\"timestampUTC\":\"TEST-UTC\",\"slamId\":\"TEST-SLAM-ID\",\"status\":\"TEST-STATUS\"}",
        actual);
  }

  @Test
  public void testGetSlamDateTime() {
    Assertions.assertNotNull(Util.getSlamDateTime());
  }

  @Test
  public void testGetRevenueStream() {
    Assertions.assertEquals("APIBI", Util.getRevenueStream("RMCS-QAINT-APIBI"));
  }
}
