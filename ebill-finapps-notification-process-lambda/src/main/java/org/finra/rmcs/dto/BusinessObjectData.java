package org.finra.rmcs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessObjectData {

  @JsonProperty("id")
  Integer id;
  @JsonProperty("namespace")
  String namespace;
  @JsonProperty("businessObjectDefinitionName")
  String businessObjectDefinitionName;
  @JsonProperty("businessObjectFormatUsage")
  String businessObjectFormatUsage;
  @JsonProperty("businessObjectFormatFileType")
  String businessObjectFormatFileType;
  @JsonProperty("businessObjectFormatVersion")
  Integer businessObjectFormatVersion;
  @JsonProperty("partitionKey")
  String partitionKey;
  @JsonProperty("partitionValue")
  String partitionValue;
  @JsonProperty("subPartitionValues")
  List<SubPartitionValue> subPartitionValues;
  @JsonProperty("version")
  Integer version;
  @JsonProperty("latestVersion")
  boolean latestVersion;
  @JsonProperty("status")
  String status;
  @JsonProperty("storageUnits")
  List<StorageUnit> storageUnits;
  @JsonProperty("attributes")
  List<Attribute> attributes;
}
