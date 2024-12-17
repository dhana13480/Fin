package org.finra.rmcs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class ReceivableReport {
  @JsonProperty("revenue_stream")
  private String revenueStream;

  @JsonProperty("check")
  private String check;

  @JsonProperty("sourceData")
  private String sourceData;

  @JsonProperty("destinationData")
  private String destinationData;

  @JsonProperty("Rejected")
  private String rejected;

  @JsonProperty("difference")
  private String difference;

  @JsonProperty("result")
  private String result;
}
