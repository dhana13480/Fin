package org.finra.rmcs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Builder
@Data
@Entity
@Table(name = "batch_job_tracking", schema = "ebill")
@AllArgsConstructor
@NoArgsConstructor
public class BatchJobEntity implements Serializable {
  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "name")
  private String name;

  @Column(name = "status")
  private String status;

  @CreationTimestamp
  @Column(name = "start_date")
  private LocalDateTime startDate;

  @UpdateTimestamp
  @Column(name = "end_date")
  private LocalDateTime endDate;
}
