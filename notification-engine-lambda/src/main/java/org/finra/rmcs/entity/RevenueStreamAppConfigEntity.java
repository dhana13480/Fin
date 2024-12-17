package org.finra.rmcs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "revenue_stream_app_config", schema = "ebill")
public class RevenueStreamAppConfigEntity {
    @Id
    @Column(name = "revenue_stream_name")
    private String revenueStreamName;
    @Column(name = "payment_gateway_biller_id")
    private String paymentGatewayBillerId;
    @Column(name = "revenue_stream_name_desc")
    private String revenueStreamNameDesc;
}



