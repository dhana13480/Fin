package org.finra.rmcs.entity;

import jakarta.persistence.Convert;
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
import org.finra.rmcs.util.JsonStringType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Entity
@Data
@Table(name = "payments.payment")
@Convert(attributeName = "json_string", converter = JsonStringType.class)
public class PaymentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(name = "payment_req_id")
  private UUID paymentReqID;

  @Column(name = "processing_revenue_stream_name")
  private String processRevenueStreamName;

  @Column(name = "revenue_stream_name")
  private String revenueStream;

  @Column(name = "transmission_id")
  private UUID transmissionId;

  @Column(name = "purchase_items", columnDefinition = "json_string")
  @JdbcTypeCode(SqlTypes.JSON)
  private String purchaseItems;

  @Column(name = "customer_id")
  private String customerID;

  @Column(name = "total_amount")
  private BigDecimal totalAmount;

  @Column(name = "payment_number")
  private String paymentNumber;

  @Column(name = "payment_status_id")
  private String paymentStatusType;

  @Column(name = "created_by")
  private String createdBy;

  @CreationTimestamp
  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "updated_by")
  private String updatedBy;

  @UpdateTimestamp
  @Column(name = "updated_date")
  private LocalDateTime updateDate;

  @Column(name = "payment_type")
  private String paymentType;

  @Column(name = "invoice_id")
  private String invoiceId;
}
