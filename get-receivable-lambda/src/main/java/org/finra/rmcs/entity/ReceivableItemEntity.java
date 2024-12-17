package org.finra.rmcs.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.finra.rmcs.constants.Constants;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.type.SqlTypes;

@Entity
@Data
@Table(name = "receivable_item", schema = "rmcs")
public class ReceivableItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @JsonProperty("receivable_id")
  @Column(name = "receivable_id")
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID receivableId;

  private Integer sequence;

  @JsonProperty("sales_item_id")
  @Column(name = "sales_item_id")
  private String salesItemId;

  @JsonProperty("transaction_date")
  @Column(name = "transaction_date")
  @JsonFormat(pattern = Constants.DATETIME_WITHOUT_MILLISECONDS_FORMAT)
  private LocalDateTime transactionDate;

  @JsonProperty("sales_item_name")
  @Column(name = "sales_item_name")
  private String salesItemName;

  private Double quantity;

  @JsonProperty("unit_amount")
  @Column(name = "unit_amount")
  private BigDecimal unitAmount;

  private BigDecimal amount;

  @JsonProperty("sales_tax")
  @Column(name = "sales_tax")
  private BigDecimal salesTax;

  private BigDecimal total;

  @JsonProperty("crd_transaction_date")
  @Column(name = "crd_transaction_date")
  @JsonFormat(pattern = Constants.DATETIME_WITHOUT_MILLISECONDS_FORMAT)
  private LocalDateTime crdTransactionDate;

  @JsonProperty("branch_id")
  @Column(name = "branch_id")
  private String branchId;

  @JsonProperty("filing_id")
  @Column(name = "filing_id")
  private String filingId;

  @JsonProperty("individual_crd_no")
  @Column(name = "individual_crd_no")
  private String individualCrdNo;

  @JsonProperty("individual_name")
  @Column(name = "individual_name")
  private String individualName;

  @JsonProperty("billing_code")
  @Column(name = "billing_code")
  private String billingCode;

  @JsonProperty("from_date")
  @Column(name = "from_date")
  @JsonFormat(pattern = Constants.DATETIME_WITHOUT_MILLISECONDS_FORMAT)
  private LocalDateTime fromDate;

  @JsonProperty("to_date")
  @Column(name = "to_date")
  @JsonFormat(pattern = Constants.DATETIME_WITHOUT_MILLISECONDS_FORMAT)
  private LocalDateTime toDate;

  @JsonProperty("supplier_invoice_no")
  @Column(name = "supplier_invoice_no")
  private String supplierInvoiceNo;

  @JsonProperty("source_trans_date")
  @Column(name = "source_trans_date")
  @JsonFormat(pattern = Constants.DATETIME_WITHOUT_MILLISECONDS_FORMAT)
  private LocalDateTime sourceTransDate;

  @JsonProperty("supplier_short_name")
  @Column(name = "supplier_short_name")
  private String supplierShortname;

  @JsonProperty("supplier_id")
  @Column(name = "supplier_id")
  private String supplierId;

  @JsonProperty("source_trans_id")
  @Column(name = "source_trans_id")
  private String sourceTransId;

  @JsonProperty("case_open_date")
  @Column(name = "case_open_date")
  @JsonFormat(pattern = Constants.DATETIME_WITHOUT_MILLISECONDS_FORMAT)
  private LocalDateTime caseOpenDate;

  @JsonProperty("extended_unit_amt")
  @Column(name = "extended_unit_amt")
  private String extendedUnitAmt;

  @JsonProperty("line_item_description")
  @Column(name = "line_item_description")
  private String lineItemDescription;

  @JsonProperty("product_category")
  @Column(name = "product_category")
  private String productCategory;

  @JsonProperty("created_by")
  @Column(name = "created_by")
  private String createdBy;

  @JsonProperty("created_date")
  @CreationTimestamp
  @Column(name = "created_date")
  @JsonFormat(pattern = Constants.DATETIME_WITHOUT_MILLISECONDS_FORMAT)
  private LocalDateTime createdDate;

  @JsonProperty("updated_by")
  @Column(name = "updated_by")
  private String updatedBy;

  @JsonProperty("updated_date")
  @UpdateTimestamp
  @Column(name = "updated_date")
  @JsonFormat(pattern = Constants.DATETIME_WITHOUT_MILLISECONDS_FORMAT)
  private LocalDateTime updatedDate;

  @JsonProperty("sales_item_name_desc")
  @Transient
  private String salesItemNameDesc;

}
