package org.finra.rmcs.constants;

import java.util.Arrays;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum HerdEntity {
  OK(Constants.RMCS_BUSINESS_DEFINATION_NAME_OK),
  FAIL(Constants.RMCS_BUSINESS_DEFINATION_NAME_FAIL),
  FATAL(Constants.RMCS_BUSINESS_DEFINATION_NAME_FATAL);

  private final String businessObjectDefinitionName;
  private final String businessObjectFormatFileType =
      Constants.RMCS_BUSINESS_OBJECT_FORMAT_FILE_TYPE;
  private final String businessObjectFormatUsage = Constants.RMCS_BUSINESS_OBJECT_USAGE;

  HerdEntity(String businessObjectDefinitionName) {
    this.businessObjectDefinitionName = businessObjectDefinitionName;
  }

  public static HerdEntity findByFileName(String fileName) {
    return Arrays.stream(values())
        .filter(
            herdEntity ->
                null != fileName
                    && StringUtils.containsAnyIgnoreCase(fileName, herdEntity.toString()))
        .findFirst()
        .orElse(null);
  }
}
