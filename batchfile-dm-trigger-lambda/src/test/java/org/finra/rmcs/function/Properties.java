package org.finra.rmcs.function;

import java.util.Map;

public final class Properties {

  private Properties() {

  }

  public static String getEnvValue(final String name) {
    return System.getenv(name);
  }

  public static Map<String, String> getEnvValue() {
    return System.getenv();
  }
}
