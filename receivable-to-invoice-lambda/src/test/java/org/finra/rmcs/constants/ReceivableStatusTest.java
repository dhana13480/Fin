package org.finra.rmcs.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.finra.rmcs.common.constants.ReceivableStatus;
import org.junit.jupiter.api.Test;

public class ReceivableStatusTest {

  @Test
  public void testStatus() {
    assertEquals(Integer.valueOf(12), ReceivableStatus.INVALID.getId());
    assertEquals(ReceivableStatus.SENT_TO_WD, ReceivableStatus.INVOICED.nextState());
    assertEquals(ReceivableStatus.SENT_TO_WD, ReceivableStatus.getEnumById(16));
  }
}
