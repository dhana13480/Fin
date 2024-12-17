package org.finra.rmcs.constants;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum BatchJobStatusEnum {
  STARTED,
  FAILED,
  COMPLETED,
  NO_DATA_NO_REPORT_GENERATION;

  public static BatchEnum getBatchJobStatusEnum(String eventName) {
    List<BatchEnum> list =
        Stream.of(BatchEnum.values())
            .filter(f -> f.name().equals(eventName))
            .collect(Collectors.toList());
    return !list.isEmpty() ? list.get(0) : null;
  }
}
