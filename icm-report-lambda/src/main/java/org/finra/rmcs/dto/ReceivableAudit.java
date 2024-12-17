package org.finra.rmcs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class ReceivableAudit {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("receivable_entry_id ")
    private UUID receivableEntryId;

    @JsonProperty("action")
    private String action;

    @JsonProperty("audit_entry_created_date")
    private ZonedDateTime auditEntryCreatedDate;

    private String company;

    @JsonProperty("revenue_stream")
    private String revenueStream;

    @JsonProperty("processing_revenue_stream")
    private String processingRevenueStream;

    @JsonProperty("total_lines")
    private String totalLines;

    @JsonProperty("invoice_id")
    private String invoiceId;

    @JsonProperty("transmission_id")
    private String transmissionId;

    private String source;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("revenue_stream_receivable_id")
    private String revenueStreamReceivableId;

    @JsonProperty("customer_id")
    private String customerId;

    private String amount;

    @JsonProperty("clearing_number")
    private String clearingNumber;

    @JsonProperty("mpid")
    private String mpid;

    @JsonProperty("transaction_date")
    private String transactionDate;

    @JsonProperty("triggered_Username")
    private String triggeredUsername;

    private Integer status;

    @JsonProperty("status_reason")
    private String statusReason;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("created_date")
    private ZonedDateTime createdDate;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("updated_date")
    private LocalDateTime updatedDate;

    @JsonProperty("invoice_status")
    private String invoiceStatus;

    @JsonProperty("invoice_date")
    private LocalDateTime invoiceDate;

    @JsonProperty("due_date")
    private LocalDateTime dueDate;

    @JsonProperty("post_date")
    private LocalDateTime accountingDate;

    @JsonProperty("balance")
    private String balance;

    @JsonProperty("invoice_type")
    private String invoiceType;

    @JsonProperty("imported")
    private LocalDateTime imported;

    private boolean legacy;

    @JsonProperty("exported")
    private String exported;

}
