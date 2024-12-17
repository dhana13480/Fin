package org.finra.rmcs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class GatewayEmailNotificationRequest {
  @NotNull
  @JsonProperty("event_name")
  String eventName;
  @NotNull
  @NotNull
  @JsonProperty("to")
  List<String> to;
  @JsonProperty("cc")
  List<String> cc;
  @JsonProperty("bcc")
  List<String> bcc;
  @NotNull
  @JsonProperty("subject")
  String subject;
  @NotNull
  @JsonProperty("body")
  String body;
  @NotNull
  @JsonProperty("feedback")
  String feedback;

}
