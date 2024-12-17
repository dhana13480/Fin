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
@Table(name= "connect_replication_status", schema = "crisp_connect_target")
public class ConnectReplicationStatusEntity implements Serializable {

  @Id
  @Column(name = "notification_id")
  private Integer notificationId;

  @Column(name = "created")
  private LocalDateTime created;

  @Column(name = "processing")
  private boolean processing;

  @Column(name = "status")
  private String status;

  @Column(name = "entity")
  private String entity;
}
