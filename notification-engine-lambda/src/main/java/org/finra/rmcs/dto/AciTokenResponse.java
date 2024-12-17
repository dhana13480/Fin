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
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class AciTokenResponse {

  @JsonProperty("url")
  private String url;

  @JsonProperty("key")
  private String key;

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("user_id")
  private String userId;


  @JsonProperty("message")
  private String message;

  @JsonProperty("errors")
  private List<String> errors;


  @JsonProperty("time_stamp")
  private String timeStamp;


 /* @JsonProperty("expires_in")
  private int expiresIn;

  @JsonProperty("token_type")
  private String tokenType;

  @JsonProperty("not-before-policy")
  private String notBeforePolicy;
*/

}

