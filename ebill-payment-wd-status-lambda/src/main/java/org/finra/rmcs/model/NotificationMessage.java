package org.finra.rmcs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.finra.rmcs.dto.Payment;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {

  @JsonProperty("revenue_stream")
  private String revenueStream;

  @JsonProperty("status")
  private String status;

  @JsonProperty("payment")
  private Payment payment;

}
