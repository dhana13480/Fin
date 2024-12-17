package org.finra.rmcs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "email_config",schema = "ebill")
public class EmailConfig {

    @Id
    @Column(name = "event_type_name")
    private String eventTypeName;

    @Column(name = "\"FROM\"")
    private String from;

    @Column(name = "\"TO\"")
    private String to;

    private String cc;

    @Column(name = "bcc")
    private String bcc;

    @Column(name = "feedback")
    private String feedBack;

    private String body;

    @Column(name = "subject")
    private String subject;

    @Column(name = "created_by")
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

}
