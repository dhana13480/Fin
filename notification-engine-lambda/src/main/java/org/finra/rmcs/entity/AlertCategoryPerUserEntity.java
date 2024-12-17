package org.finra.rmcs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "alert_config_per_user",schema = "ebill")
public class AlertCategoryPerUserEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "alert_rule_id")
    private Integer alertRuleID;
    @Column(name = "threshold_value")
    private String thresholdValue;
    @Column(name = "threshold_criteria")
    private String thresholdCriteria;
    @Column(name = "enable")
    private String enable;
    @Column(name = "ews_user_id")
    private String ewsUserID;

    @Column(name = "org_id")
    private String orgID;

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


}