package org.finra.rmcs.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "receivable")
public class ReceivableEntity {
  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "revenue_stream_receivable_id")
  private String revenueStreamReceivableId;

  private String company;

  @Column(name = "customer_id")
  private String customerId;

  @Column(name = "revenue_stream")
  private String revenueStream;

  @Column(name = "invoice_id")
  private String invoiceId;

  @Column(name = "total_lines")
  private Integer totalLine;

  private BigDecimal amount;

  @Column(name = "clearing_number")
  private String clearingNumber;

  @Column(name = "mp_id")
  private String mpID;

  @Column(name = "transaction_date")
  private LocalDateTime transactionDate;

  @Column(name = "transmission_id")
  private String transmissionId;

  private Integer status;

  @Column(name = "status_reason")
  private String statusReason;

  @Column(name = "created_by")
  private String createdBy;

  @CreationTimestamp
  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "updated_by")
  private String updatedBy;

  @UpdateTimestamp
  @Column(name = "updated_date")
  private LocalDateTime updatedDate;

  @Column(name = "invoice_status")
  private String invoiceStatus;

  @Column(name = "invoice_date")
  private LocalDateTime invoiceDate;

  @Column(name = "due_date")
  private LocalDateTime dueDate;

  @Column(name = "accounting_date")
  private LocalDateTime accountingDate;

  @Column(name = "post_date")
  private LocalDateTime postDate;

  @Column(name = "balance")
  private String balance;

  @Column(name = "invoice_type")
  private String invoiceType;

  @Column(name = "imported")
  private String imported;

  @Column(name = "legacy")
  private boolean legacy;

  @Column(name = "exported")
  private String exported;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "receivable_id", referencedColumnName = "id")
  private List<ReceivableItemEntity> receivableItems;
}
