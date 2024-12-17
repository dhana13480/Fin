package org.finra.rmcs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class NotificationMessage {
  @JsonProperty("ews_user")
  private  String ewsUser;

  @JsonProperty("module")
  private  String module;

  @JsonProperty("status")
  private String status;

  @JsonProperty("payment_number")
  private List<String> paymentNumber;

  @JsonProperty("transmission_id")
  private String transmissionId;

  @JsonProperty("amount")
  private String amount;

  @JsonProperty("alert_event_email_id")
  private UUID alertEventEmailId;

  @JsonProperty("subscription_id")
  private String subscriptionId;

  @JsonProperty("error_msg")
  private List<String> errorMsg;

  @JsonProperty("invoice_type")
  private String invoiceType;

  @JsonProperty("invoice_number")
  private List<String> invoiceNumber;

}
