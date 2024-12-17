package org.finra.rmcs.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonPropertyOrder({
  "id",
  "company",
  "revenue_stream",
  "invoice_id",
  "customer_id",
  "invoice_status",
  "invoice_date",
  "due_date",
  "accounting_date",
  "total_lines",
  "post_date",
  "amount",
  "balance",
  "invoice_type",
  "created",
  "updated",
  "imported",
  "legacy",
  "exported",
  "clearing_number",
  "mpid",
  "lines"
})
public class Receivable implements Cloneable {

  @JsonIgnore private UUID id;

  @JsonIgnore private String revenueStreamReceivableId;
  private String company;

  @JsonProperty(value = "id")
  private String wdId;

  @JsonProperty(value = "customer_id")
  private String customerId;

  @JsonProperty(value = "revenue_stream")
  private String revenueStream;

  @JsonProperty(value = "invoice_id")
  private String invoiceId;

  @JsonProperty(value = "total_lines")
  private Integer totalLine;

  private BigDecimal amount;

  @JsonProperty(value = "clearing_number")
  private String clearingNumber;

  @JsonProperty(value = "mpid")
  private String mpID;

  @CreationTimestamp
  @JsonProperty(value = "created")
  private LocalDateTime createdDate;

  @UpdateTimestamp
  @JsonProperty(value = "updated")
  private LocalDateTime updatedDate;

  @JsonProperty(value = "invoice_status")
  private String invoiceStatus;

  @JsonProperty(value = "invoice_date")
  private String invoiceDate;

  @JsonProperty(value = "due_date")
  private LocalDateTime dueDate;

  @JsonProperty(value = "accounting_date")
  private LocalDateTime accountingDate;

  @JsonProperty(value = "post_date")
  private LocalDateTime postDate;

  @JsonProperty(value = "balance")
  private String balance;

  @JsonProperty(value = "invoice_type")
  private String invoiceType;

  @JsonProperty(value = "imported")
  private String imported;

  @JsonProperty(value = "legacy")
  private boolean legacy;

  @JsonProperty(value = "exported")
  private String exported;

  @Transient private List<ReceivableItem> lines;

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
