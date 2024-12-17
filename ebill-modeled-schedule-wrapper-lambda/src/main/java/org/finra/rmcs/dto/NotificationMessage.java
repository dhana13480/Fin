package org.finra.rmcs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationMessage {

  @JsonProperty("ews_user")
  private  String userId;

  @JsonProperty("module")
  private  String module;

  @JsonProperty("status")
  private String status;

  @JsonProperty("payment_number")
  private List<String> paymentNumber;

  @JsonProperty("subscription_id")
  private String subscriptionId;

  @JsonProperty("error_msg")
  private List<String> errorMsg;

  @JsonProperty("transmission_id")
  private String transmissionId;
  @JsonProperty("alert_event_email_id")
  private UUID alertEventEmailId;

  @JsonProperty("amount")
  private String amount;
  @JsonProperty("invoice_type")
  private String invoiceType;
  @JsonProperty("invoice_number")
  private List<String> invoiceNumber;

}
