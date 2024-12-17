package org.finra.rmcs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "data_refresh_log", schema = "modeled")
public class DataRefreshLogEntity implements Serializable {

  @Id
  @Column(name = "id")
  private Integer id;

  @Column(name = "refresh_task_name")
  private String refreshTaskName;

  @Column(name = "status")
  private String status;

  @Column(name = "start_time")
  private LocalDateTime startTime;

  @Column(name = "end_time")
  private LocalDateTime endTime;

  @Column(name = "rec_processed")
  private Integer recProcessed;

  @Column(name = "parent_task_id")
  private Integer parentTaskId;

  @Column(name = "message")
  private String message;


}
