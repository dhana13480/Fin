package org.finra.rmcs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@Table(name = "accounting_equation_ff_view", schema = "modeled")
@AllArgsConstructor
@NoArgsConstructor
public class AccountingEquationFFViewEntity implements Serializable {
    @Id
    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "revenue_stream")
    private String revenueStream;


    @Column(name = "beginning_balance")
    private BigDecimal beginningBalance;

    @Column(name = "ending_balance")
    private BigDecimal endingBalance;

    @Column(name = "calculated_balance")
    private BigDecimal calculatedBalance;

    @Column(name = "variance")
    private BigDecimal variance;

}
