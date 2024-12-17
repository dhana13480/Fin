package org.finra.rmcs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@Table(name = "accounting_equation_invoice_view", schema = "modeled")
@AllArgsConstructor
@NoArgsConstructor
public class AccountingEquationInvoiceViewEntity implements Serializable {
    @Id
    @Column(name = "invoice_id")
    private String invoiceId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "revenue_stream")
    private String revenueStream;

    @Column(name = "org_id")
    private String orgId;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "invoice_amount")
    private BigDecimal invoiceAmount;

    @Column(name = "invoice_balance")
    private BigDecimal invoiceBalance;

    @Column(name = "calculated_invoice_balance")
    private BigDecimal calculatedInvoiceBalance;

    @Column(name = "variance")
    private BigDecimal variance;

}
