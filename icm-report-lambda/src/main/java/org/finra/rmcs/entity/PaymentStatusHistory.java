package org.finra.rmcs.entity;

import jakarta.persistence.Convert;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.finra.rmcs.util.JsonStringType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;


@Builder
@Data
@Entity
@Table(name = "payment_status_history", schema = "payments")
@Convert(attributeName = "json_string", converter = JsonStringType.class)
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatusHistory implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @CreationTimestamp
  @Column(name = "created_timestamp")
  private LocalDateTime createdDate;

  @Column(name = "confirmation_number")
  private String confirmationNumber;

  @Column(name = "process_name")
  private String processName;

  @Column(name = "method_name")
  private String methodName;

  @Column(name = "payment_status_new")
  private Integer paymentStatusNew;

  @Column(name = "payment_status_old")
  private Integer paymentStatusOld;

  @Column(name = "payment_status_published_flag")
  private Boolean paymentStatusPublishedFlag;

  @Column(name = "payment_status_published_timestamp")
  private LocalDateTime paymentStatusPublishedTimeStamp;

  @Column(name = "remit_file_report_id")
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID remitFileReportId;

  @Column(name = "payment_status_reason_new")
  private Integer paymentStatusReasonNew;

  @Column(name = "payment_status_reason_old")
  private Integer paymentStatusReasonOld;

  @Column(name = "remit_file_transaction_id")
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID remitFileTransactionId;

  @Column(name = " workday_notify_status_id")
  private BigDecimal workdayNotifyStatusId;

  @Column(name = "payment_id")
  private UUID paymentId;
}
