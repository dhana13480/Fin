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
public class StorageUnit {
  @JsonProperty("storage")
  Storage storage;
  @JsonProperty("storageDirectory")
  StorageDirectory storageDirectory;
  @JsonProperty("storageFiles")
  List<StorageFile> storageFiles;
  @JsonProperty("storageUnitStatus")
  String storageUnitStatus;
}
