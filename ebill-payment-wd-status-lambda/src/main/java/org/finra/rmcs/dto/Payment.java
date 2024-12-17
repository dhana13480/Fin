package org.finra.rmcs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payment {

  private UUID id;

  @JsonProperty("payment_req_id")
  private UUID paymentReqID;

  @JsonProperty("processing_revenue_stream_name")
  private String processRevenueStreamName;

  @JsonProperty("revenue_stream_name")
  private String revenueStreamName;

  @JsonProperty("transmission_id")
  private UUID transmissionId;

  @JsonProperty("purchase_items")
  private String purchaseItems;

  @JsonProperty("customer_id")
  private String customerID;

  @JsonProperty("total_amount")
  private BigDecimal totalAmount;

  @JsonProperty("payment_status_id")
  private Integer paymentStatusID;

  @JsonProperty("created_by")
  private String createdBy;

  @CreationTimestamp
  @JsonProperty("created_date")
  private LocalDateTime createdDate;

  @JsonProperty("updated_by")
  private String updatedBy;

  @UpdateTimestamp
  @JsonProperty("updated_date")
  private LocalDateTime updatedDate;

  @JsonProperty("workday_notify_status_id")
  private Integer workdayNotifyStatusId;

  @JsonProperty("invoice_id")
  private String invoiceId;

  @JsonProperty("payment_number")
  private String paymentNumber;

  @JsonProperty("payment_type")
  private String paymentType;

  @JsonProperty("ebill_id")
  private String ebillId;

}
