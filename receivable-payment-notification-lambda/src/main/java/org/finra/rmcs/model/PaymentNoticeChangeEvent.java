package org.finra.rmcs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentNoticeChangeEvent {
  @JsonProperty("payment_req_id")
  private String paymentReqId;

  @JsonProperty("payment_id")
  private List<String> paymentId;

  @JsonProperty("event_name")
  private String eventName;

  @JsonProperty("payment_number")
  private List<String> paymentNumber;

  @JsonProperty("transmission_id")
  private String transmissionId;

  @JsonProperty("processing_revenue_stream")
  private String processingRevenueStream;

  @JsonProperty("confirmation_number")
  private List<String> confirmationNumber;

  @JsonProperty("invoice_id")
  private List<String> invoiceId;

  @JsonProperty("customer_ids")
  private List<String> customerIds;
}
