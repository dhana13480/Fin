package org.finra.rmcs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageFile {
  @JsonProperty("filePath")
  String filePath;
  @JsonProperty("fileSizeBytes")
  String fileSizeBytes;
}
