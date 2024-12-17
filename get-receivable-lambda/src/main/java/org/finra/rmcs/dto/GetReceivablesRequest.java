package org.finra.rmcs.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class GetReceivablesRequest {

  @JsonProperty("correlation_id")
  private String correlationId;

  @JsonProperty("invoice")
  private List<GetReceivablesRequestInvoiceId> ids = new ArrayList<>();

  @JsonProperty("context")
  private TokenContext context;

  private UUID transmissionId;

  public List<String> getInvoiceIds() {
    return ids.stream().map(i -> i.getId()).collect(Collectors.toList());
  }

}
