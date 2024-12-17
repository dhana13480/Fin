package org.finra.rmcs.util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.StringUtils;
import org.finra.fidelius.FideliusClient;
import org.finra.rmcs.util.TestUtil.Appender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

public class UtilTest {

  @Test
  public void testGetPassword_whenSuccess() {
    Appender appender = TestUtil.setUpLogMonitorForClass(Util.class);
    String expected = "testPassword";
    String key = "testKey";
    try (MockedConstruction<FideliusClient> ignored =
        Mockito.mockConstruction(
            FideliusClient.class,
            (mock, context) ->
                when(mock.getCredential(any(), any(), any(), any(), any())).thenReturn(expected))) {
      Assertions.assertEquals(expected, Util.getPassword(key, StringUtils.EMPTY));
      Assertions.assertEquals(
          String.format("Successfully retrieved password of %s from Fidelius", key),
          appender.getEvents().get(0).getFormattedMessage());
    }
  }

  @Test
  public void testGetPassword_whenFail() {
    Appender appender = TestUtil.setUpLogMonitorForClass(Util.class);
    String expected = StringUtils.EMPTY;
    String key = "testKey";
    try (MockedConstruction<FideliusClient> ignored =
        Mockito.mockConstruction(
            FideliusClient.class,
            (mock, context) ->
                when(mock.getCredential(any(), any(), any(), any(), any())).thenReturn(expected))) {
      Assertions.assertEquals(expected, Util.getPassword(key, StringUtils.EMPTY));
      Assertions.assertEquals(
          String.format("Failed to retrieve password of %s from Fidelius", key),
          appender.getEvents().get(0).getFormattedMessage());
    }
  }
}
