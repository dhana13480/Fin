package org.finra.rmcs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessObjectDataKey {
  private String namespace;
  private String businessObjectDefinitionName;
  private String businessObjectFormatUsage;
  private String businessObjectFormatFileType;
  private String businessObjectFormatVersion;
  private String partitionValue;
  private Integer businessObjectDataVersion;
  private List<String> subPartitionValues;
}
