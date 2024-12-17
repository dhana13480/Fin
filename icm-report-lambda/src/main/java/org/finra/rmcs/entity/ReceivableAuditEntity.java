package org.finra.rmcs.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity(name = "receivableAudit")
@Table(name = "receivable_audit", schema = "rmcs")
public class ReceivableAuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private UUID id;

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "receivable_entry_id ")
  private UUID receivableEntryId;

  @Column(name = "action")
  private String action;

  @CreationTimestamp
  @Column(name = "audit_entry_created_date")
  private ZonedDateTime auditEntryCreatedDate;

  @Column(name = "revenue_stream_receivable_id")
  private String revenueStreamReceivableId;

  private String company;

  @Column(name = "customer_id")
  private String customerId;

  @Column(name = "revenue_stream")
  private String revenueStream;

  @Column(name = "processing_revenue_stream")
  private String processingRevenueStream;

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
  private ZonedDateTime createdDate;

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

  private String source;

  @Column(name = "payment_received")
  private String paymentReceived;


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReceivableAuditEntity that = (ReceivableAuditEntity) o;
    return legacy == that.legacy
        && Objects.equals(receivableEntryId, that.receivableEntryId)
        && Objects.equals(action, that.action)
        && Objects.equals(revenueStreamReceivableId, that.revenueStreamReceivableId)
        && Objects.equals(company, that.company)
        && Objects.equals(customerId, that.customerId)
        && Objects.equals(revenueStream, that.revenueStream)
        && Objects.equals(processingRevenueStream, that.processingRevenueStream)
        && Objects.equals(invoiceId, that.invoiceId)
        && Objects.equals(totalLine, that.totalLine)
        && Objects.equals(amount, that.amount)
        && Objects.equals(clearingNumber, that.clearingNumber)
        && Objects.equals(mpID, that.mpID)
        && Objects.equals(transactionDate, that.transactionDate)
        && Objects.equals(transmissionId, that.transmissionId)
        && Objects.equals(status, that.status)
        && Objects.equals(statusReason, that.statusReason)
        && Objects.equals(invoiceStatus, that.invoiceStatus)
        && Objects.equals(invoiceDate, that.invoiceDate)
        && Objects.equals(dueDate, that.dueDate)
        && Objects.equals(accountingDate, that.accountingDate)
        && Objects.equals(postDate, that.postDate)
        && Objects.equals(balance, that.balance)
        && Objects.equals(invoiceType, that.invoiceType)
        && Objects.equals(imported, that.imported)
        && Objects.equals(exported, that.exported)
        && Objects.equals(source, that.source);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        receivableEntryId,
        action,
        revenueStreamReceivableId,
        company,
        customerId,
        revenueStream,
        processingRevenueStream,
        invoiceId,
        totalLine,
        amount,
        clearingNumber,
        mpID,
        transactionDate,
        transmissionId,
        status,
        statusReason,
        invoiceStatus,
        invoiceDate,
        dueDate,
        accountingDate,
        postDate,
        balance,
        invoiceType,
        imported,
        legacy,
        exported,
        source);
  }
}
