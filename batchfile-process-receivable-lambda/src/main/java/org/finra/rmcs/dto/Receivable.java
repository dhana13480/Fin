package org.finra.rmcs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Receivable {

  @JsonProperty("transmission_id")
  private String transmissionId;

  @JsonProperty("source")
  private String source;

  @JsonProperty("user_name")
  private String userName;

  @JsonProperty("id")
  private String id;

  @JsonProperty("company")
  private String company;

  @JsonProperty("revenue_stream")
  private String revenueStream;

  @JsonProperty("customer_id")
  private String customerId;

  @JsonProperty("invoice_id")
  private String invoiceId;

  @JsonProperty("total_lines")
  private String totalLines;

  @JsonProperty("amount")
  private String amount;

  @JsonProperty("clearing_number")
  private String clearingNumber;

  @JsonProperty("mpid")
  private String mpid;

  @JsonProperty("create_timestamp")
  private String transactionDate;

  @JsonProperty("triggered_Username")
  private String triggeredUsername;

  @JsonProperty("items")
  private List<ReceivableItem> receivableItems;
}
