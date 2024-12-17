package org.finra.rmcs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailResponse {
  
  private GatewayEmailNotificationResponse[] gatewayEmailNotificationResponseList;
  @JsonProperty("message")
  private String message;

  @JsonProperty("errors")
  private List<String> errors;

  @JsonProperty("time_stamp")
  private String timeStamp;

}
