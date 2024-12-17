package org.finra.rmcs.entity;

import jakarta.persistence.Convert;
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
import org.hibernate.type.SqlTypes;

@Table(name = "receivable_req")
@Data
@Entity
@Convert(attributeName = "json_string", converter = JsonStringType.class)
public class ReceivableReqEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(name = "created_by")
  private String createdBy;

  @CreationTimestamp
  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "request_payload", columnDefinition = "json_string")
  @JdbcTypeCode(SqlTypes.JSON)
  private String requestPayload;

  @Column(name = "transmission_id")
  private String transmissionId;

  @Column(name = "transaction_date")
  private LocalDateTime transactionDate;

  private String source;

  @Column(name = "user_name")
  private String userName;

  @Column(name = "revenue_stream")
  private String revenueStream;

  @Column(name = "last_lambda")
  private String lastLambda;

  @Column(name = "file_url")
  private String fileUrl;

  private String status;

  @Column(name = "response_payload", columnDefinition = "json_string")
  @JdbcTypeCode(SqlTypes.JSON)
  private String responsePayload;

  @Column(name = "revenue_stream_receivable_id")
  private String revenueStreamReceivableId;
}
