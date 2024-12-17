package org.finra.rmcs.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;

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

}
