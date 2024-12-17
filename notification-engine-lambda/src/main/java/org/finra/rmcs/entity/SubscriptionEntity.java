package org.finra.rmcs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Builder
@Data
@Entity
@Table(name = "subscription", schema = "ebill")
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "org_id")
    private String orgId;

    @Column(name = "ews_user_id")
    private String ewsUserId;

    @Column(name = "effective_start_date")
    private LocalDateTime effectiveStartDate;

    @Column(name = "effective_end_date")
    private LocalDateTime effectiveEndDate;

    @Column(name = "request_json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String requestJson;

    @Column(name = "payment_type_id")
    private Integer paymentTypeId;

    @Column(name = "funding_account_id")
    private UUID fundingAccountId;

    @Column(name = "delete_subscription")
    private Boolean deleteSubscription;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updateDate;

    @Column(name = "deletion_reason")
    private String deletionReason;
}
