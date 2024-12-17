package org.finra.rmcs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EbillRunDataRefreshResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("user_id")
    private String ewsUserId;

    @JsonProperty("errors")
    private List<String> errors;

    @JsonProperty("time_stamp")
    private String timeStamp;
}
