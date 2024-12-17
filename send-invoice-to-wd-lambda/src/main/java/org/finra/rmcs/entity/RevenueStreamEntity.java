package org.finra.rmcs.entity;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "revenue_stream")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueStreamEntity implements Serializable {

  private static final long serialVersionUID = -353654023165677518L;

  @Id
  @Column(name = "revenue_stream_name", unique = true, nullable = false)
  private String revenueStreamName;

  @Column(name = "payment_validation")
  private boolean paymentValidation;

  @Column(name = "send_to_wd")
  private boolean sendToWD;

  @Column(name = "parallel_run")
  private boolean parallelRun;
}
