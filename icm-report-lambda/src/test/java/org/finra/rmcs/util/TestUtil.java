package org.finra.rmcs.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

@Slf4j
public class TestUtil {

  public static String getResourceFileContents(String fileName) throws Exception {
    try {
      InputStream is = TestUtil.class.getResourceAsStream(fileName);
      InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
      BufferedReader reader = new BufferedReader(streamReader);
      StringBuilder buffer = new StringBuilder();
      for (String line; (line = reader.readLine()) != null; ) {
        buffer.append(line).append("\n");
      }
      return buffer.toString();
    } catch (Exception e) {
      log.error(String.format(" ### Error Reading Test File: %s ###", fileName), e);
      throw e;
    }
  }

  public static String getResourceContent(String fileName) throws IOException {
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    try (InputStream is = classLoader.getResourceAsStream(fileName)) {
      if (is == null) {
        return null;
      }
      try (InputStreamReader isr = new InputStreamReader(is);
          BufferedReader reader = new BufferedReader(isr)) {
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
      }
    }
  }

  public static Appender setUpLogMonitorForClass(Class<?> clazz) {
    Appender appender = new Appender();
    Logger logger = (Logger) LoggerFactory.getLogger(clazz);
    logger.addAppender(appender);
    appender.start();
    return appender;
  }

  @Getter
  public static class Appender extends AppenderBase<ILoggingEvent> {

    private final List<ILoggingEvent> events = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
      events.add(iLoggingEvent);
    }
  }
}
