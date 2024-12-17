package org.finra.rmcs.constants;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum BatchEnum {
  ACCOUNTING_EQUATION_REPORT_JOB;

  public static BatchEnum getBatchEnum(String eventName) {
    List<BatchEnum> list =
        Stream.of(BatchEnum.values())
            .filter(f -> f.name().equals(eventName))
            .collect(Collectors.toList());
    return !list.isEmpty() ? list.get(0) : null;
  }
}
