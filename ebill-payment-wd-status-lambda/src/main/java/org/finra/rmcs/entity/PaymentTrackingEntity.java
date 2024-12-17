package org.finra.rmcs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "payment_tracking", schema = "ebill")
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTrackingEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private UUID id;

  @Column(name = "bus_un")
  private String businessUnit;

  @Column(name = "org_id")
  private String orgId;

  @Column(name = "invc_id")
  private String invoiceId;

  @Column(name = "pymnt_dt")
  private LocalDate paymentDate;

  @Column(name = "pymnt_amt")
  private BigDecimal paymentAmount;

  @Column(name = "pymnt_st_id")
  private Integer paymentStatusId;

  @Column(name = "pymnt_type_id")
  private Integer paymentTypeId;

  @Column(name = "pymnt_rfrnc_nb")
  private String paymentReferenceNumber;

  @Column(name = "prcs_rspns_tx")
  private String prcsRspnsTx;

  @Column(name = "customer_id")
  private String customerId;

  @Column(name = "ralcn_src_bus_un")
  private String ralcnSrcBusUn;

  @Column(name = "auto_pymnt_fl")
  private String autoPymntFl;

  @CreationTimestamp
  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @UpdateTimestamp
  @Column(name = "updated_date")
  private LocalDateTime updatedDate;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "updated_by")
  private String updatedBy;

  @Column(name = "send_to_wd")
  private Boolean sentToWorkday;

  @Column(name = "wd_sent_ts")
  private LocalDate sentToWorkdayTimestamp;

  @Column(name = "request_desc")
  private String requestDescription;

  @Column(name = "wd_status")
  private String wdStatus;

}
