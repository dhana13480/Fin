package org.finra.rmcs.entity;

import jakarta.persistence.Convert;
import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.finra.rmcs.util.JsonStringType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@Table(name = "receivable_json_file")
@Convert(attributeName = "json_string", converter = JsonStringType.class)
public class ReceivableJsonFileEntity {

  @Id private UUID id;

  @Column(name = "s3_file_url")
  private String s3FileUrl;

  @Column(name = "version")
  private Integer version;

  @Column(name = "revenue_stream")
  private String revenueStream;

  @Column(name = "dm_data_status_change_event")
  private String dmDataStatusChangeEvent;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "response_payload", columnDefinition = "json_string")
  private String responsePayload;

  @Column(name = "transmission_id")
  private String transmissionId;

  @Column(name = "status")
  private String status;

  @Column(name = "message")
  private String message;

  @Column(name = "created_by")
  private String createdBy;

  @CreationTimestamp
  @Column(name = "created_date")
  private LocalDateTime createdDate;
}
