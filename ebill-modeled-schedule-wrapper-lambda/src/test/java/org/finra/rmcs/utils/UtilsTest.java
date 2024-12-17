package org.finra.rmcs.utils;

import org.finra.rmcs.dto.NotificationMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class UtilsTest {
  @InjectMocks
  private Utils utils;

  @Test
  public void testConstructNotificationMessage() throws Exception {
    NotificationMessage snsMsg = utils.constructSNSMessage("correlationID","AFT","SUBMITTED");

    Assertions.assertNotNull(snsMsg);
  }

}
