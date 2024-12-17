package org.finra.rmcs.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@Table(name = "receivable_item")
public class ReceivableItemEntity {
  @Id private UUID id;

  @Column(name = "receivable_id")
  private UUID receivableId;

  private Integer sequence;

  @Column(name = "sales_item_id")
  private String salesItemId;

  @Column(name = "transaction_date")
  private LocalDateTime transactionDate;

  @Column(name = "sales_item_name")
  private String salesItemName;

  private Double quantity;

  @Column(name = "unit_amount")
  private BigDecimal unitAmount;

  private BigDecimal amount;

  @Column(name = "sales_tax")
  private BigDecimal salesTax;

  private BigDecimal total;

  @Column(name = "crd_transaction_date")
  private LocalDateTime crdTransactionDate;

  @Column(name = "branch_id")
  private String branchId;

  @Column(name = "filing_id")
  private String filingId;

  @Column(name = "individual_crd_no")
  private String individualCrdNo;

  @Column(name = "individual_name")
  private String individualName;

  @Column(name = "billing_code")
  private String billingCode;

  @Column(name = "from_date")
  private ZonedDateTime fromDate;

  @Column(name = "to_date")
  private ZonedDateTime toDate;

  @Column(name = "supplier_invoice_no")
  private String supplierInvoiceNo;

  @Column(name = "source_trans_date")
  private LocalDateTime sourceTransDate;

  @Column(name = "supplier_short_name")
  private String supplierShortname;

  @Column(name = "supplier_id")
  private String supplierId;

  @Column(name = "source_trans_id")
  private String sourceTransId;

  @Column(name = "case_open_date")
  private LocalDateTime caseOpenDate;

  @Column(name = "extended_unit_amt")
  private String extendedUnitAmt;

  @Column(name = "line_item_description")
  private String lineItemDescription;

  @Column(name = "product_category")
  private String productCategory;

  @Column(name = "created_by")
  private String createdBy;

  @CreationTimestamp
  @Column(name = "created_date")
  private LocalDateTime createdDate;

  @Column(name = "updated_by")
  private String updatedBy;

  @UpdateTimestamp
  @Column(name = "updated_date")
  private LocalDateTime updatedDate;
}
