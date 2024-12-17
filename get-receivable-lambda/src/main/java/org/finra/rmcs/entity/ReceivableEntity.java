package org.finra.rmcs.entity;

import jakarta.persistence.Convert;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.util.JsonStringType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.type.SqlTypes;

@Data
@Entity(name = "receivable")
@Table(name = "receivable", schema = "rmcs")
@AllArgsConstructor
@NoArgsConstructor
public class ReceivableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @JsonProperty("revenue_stream_receivable_id")
  @Column(name = "revenue_stream_receivable_id")
  private String revenueStreamReceivableId;

  private String company;

  @JsonProperty("customer_id")
  @Column(name = "customer_id")
  private String customerId;

  @JsonProperty("revenue_stream")
  @Column(name = "revenue_stream")
  private String revenueStream;

  @JsonProperty("processing_revenue_stream")
  @Column(name = "processing_revenue_stream")
  private String processingRevenueStream;

  @JsonProperty("invoice_id")
  @Column(name = "invoice_id")
  private String invoiceId;

  @JsonProperty("total_lines")
  @Column(name = "total_lines")
  private Integer totalLine;

  private BigDecimal amount;

  @JsonProperty("clearing_number")
  @Column(name = "clearing_number")
  private String clearingNumber;

  @JsonProperty("")
  @Column(name = "mp_id")
  private String mpID;

  @JsonProperty("transaction_date")
  @Column(name = "transaction_date")
  @JsonFormat(pattern = Constants.DATETIME_WITHOUT_MILLISECONDS_FORMAT)
  private LocalDateTime transactionDate;

  @JsonProperty("transmission_id")
  @Column(name = "transmission_id")
  private String transmissionId;

  private Integer status;

  @JsonProperty("status_reason")
  @Column(name = "status_reason")
  private String statusReason;

  @JsonProperty("created_by")
  @Column(name = "created_by")
  private String createdBy;

  @JsonProperty("")
  @CreationTimestamp
  @Column(name = "created_date")
  @JsonFormat(pattern = Constants.DATETIME_WITHOUT_MILLISECONDS_FORMAT)
  private LocalDateTime createdDate;

  @JsonProperty("updated_by")
  @Column(name = "updated_by")
  private String updatedBy;

  @JsonProperty("updated_date")
  @UpdateTimestamp
  @Column(name = "updated_date")
  @JsonFormat(pattern = Constants.DATETIME_WITHOUT_MILLISECONDS_FORMAT)
  private LocalDateTime updatedDate;

  @JsonProperty("invoice_status")
  @Column(name = "invoice_status")
  private String invoiceStatus;

  @JsonProperty("invoice_date")
  @Column(name = "invoice_date")
  @JsonFormat(pattern = Constants.DATETIME_WITHOUT_MILLISECONDS_FORMAT)
  private LocalDateTime invoiceDate;

  @JsonProperty("due_date")
  @Column(name = "due_date")
  @JsonFormat(pattern = Constants.DATETIME_WITHOUT_MILLISECONDS_FORMAT)
  private LocalDateTime dueDate;

  @JsonProperty("accounting_date")
  @Column(name = "accounting_date")
  @JsonFormat(pattern = Constants.DATETIME_WITHOUT_MILLISECONDS_FORMAT)
  private LocalDateTime accountingDate;

  @JsonProperty("post_date")
  @Column(name = "post_date")
  @JsonFormat(pattern = Constants.DATETIME_WITHOUT_MILLISECONDS_FORMAT)
  private LocalDateTime postDate;

  @Column(name = "balance")
  private String balance;

  @JsonProperty("invoice_type")
  @Column(name = "invoice_type")
  private String invoiceType;

  @Column(name = "imported")
  private String imported;

  @Column(name = "legacy")
  private boolean legacy;

  @Column(name = "exported")
  private String exported;

  @JsonProperty("source")
  private String source;

  @JsonProperty("items")
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "receivable_id", referencedColumnName = "id")
  private List<ReceivableItemEntity> receivableItems;
}
