package org.finra.rmcs.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;
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

  @Column(name = "send_to_wd")
  private Boolean sentToWorkday;

  @Column(name = "wd_sent_ts")
  private LocalDate sentToWorkdayTimestamp;

  @Column(name = "prcs_rspns_tx")
  private String processResponseText;

  @Column(name = "aft_to_org_id")
  private String aftToOrgId;

  @Column(name = "customer_id")
  private String customerId;

  @Column(name = "ralcn_src_bus_un")
  private String reallocationSourceBusinessUnit;

  @Column(name = "auto_pymnt_fl")
  private String autoPaymentFlag;

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

  @Column(name = "request_desc")
  private String requestDescription;

}
