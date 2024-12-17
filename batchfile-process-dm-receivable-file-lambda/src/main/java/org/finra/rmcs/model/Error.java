package org.finra.rmcs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Error {

  @JsonProperty("error_code")
  private String errorCode;

  @JsonProperty("error_message")
  private String errorMessage;

  @JsonProperty("transmission_id")
  private String transmissionId;

  @JsonProperty("time_stamp")
  private String timeStamp;
}
