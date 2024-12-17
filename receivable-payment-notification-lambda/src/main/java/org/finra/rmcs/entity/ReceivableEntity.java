package org.finra.rmcs.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Entity(name = "receivable")
@Table(name = "receivable")
public class ReceivableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
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

  @CreationTimestamp
  @Column(name = "created_date")
  private LocalDateTime createdDate;

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

  @Column(name = "payment_received")
  private String paymentReceived;
}
