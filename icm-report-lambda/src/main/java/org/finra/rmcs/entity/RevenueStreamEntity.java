package org.finra.rmcs.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
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

  @Column(name = "revenue_stream_name_desc")
  private String revenueStreamNameDesc;

  @Column(name = "created_date")
  private LocalDateTime createdDate;

  private String company;

  @Column(name = "validate_customer")
  private boolean validateCustomer;

  @Column(name = "payment_validation")
  private boolean paymentValidation;
}
