package org.finra.rmcs.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "alert_email_events",schema = "ebill")
public class AlertEmailEventEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "ews_user_id")
    private String ewsUserID;
    @Column(name = "org_id")
    private String orgID;
    @Column(name = "dstnt_email")
    private String destinationEmail;
    @Column(name = "send_ts")
    private LocalDate SendTS;

    @Column(name = "actual_value")
    private String ActualValue;

    @Column(name = "expected_value")
    private String ExpectedValue;

    @Column(name = "email_type_name")
    private String EmailTypeName;
    @Column(name = "status")
    private Integer Status;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updateDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "extra_info")
    @JdbcTypeCode(SqlTypes.JSON)
    private String extraInfo;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "alert_config_per_user_id", referencedColumnName = "id")
    private AlertCategoryPerUserEntity alertCategoryperUser;


}
