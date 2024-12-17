package org.finra.rmcs.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonPropertyOrder({
  "sequence",
  "sales_item_id",
  "source_id",
  "sales_items_name",
  "quantity",
  "unit_amt",
  "amount",
  "sales_tax",
  "total",
  "crd_transaction_date",
  "branch_id",
  "filing_id",
  "individual_crd_no",
  "individual_name",
  "billing_code",
  "from_date",
  "to_date",
  "supplier_invoice_no",
  "source_trans_date",
  "supplier_short_name",
  "supplier_id",
  "source_trans_id",
  "case_open_date",
  "extended_unit_amt",
  "line_item_description",
  "product_category"
})
public class ReceivableItem {

  @JsonIgnore private UUID id;
  @JsonIgnore private UUID receivableId;
  private Integer sequence;

  @JsonProperty(value = "sales_item_id")
  private String salesItemId;

  @JsonIgnore private LocalDateTime transactionDate;

  @JsonProperty(value = "source_id")
  private String sourceId;

  @JsonProperty(value = "sales_items_name")
  private String salesItemName;

  private Double quantity;

  @JsonProperty(value = "unit_amt")
  private BigDecimal unitAmount;

  private BigDecimal amount;

  @JsonProperty(value = "sales_tax")
  private BigDecimal salesTax;

  private BigDecimal total;

  @JsonProperty(value = "crd_transaction_date")
  private String crdTransactionDate;

  @JsonProperty(value = "branch_id")
  private String branchId;

  @JsonProperty(value = "filing_id")
  private String filingId;

  @JsonProperty(value = "individual_crd_no")
  private String individualCrdNo;

  @JsonProperty(value = "individual_name")
  private String individualName;

  @JsonProperty(value = "billing_code")
  private String billingCode;

  @JsonProperty(value = "from_date")
  private String fromDate;

  @JsonProperty(value = "to_date")
  private String toDate;

  @JsonProperty(value = "supplier_invoice_no")
  private String supplierInvoiceNo;

  @JsonProperty(value = "source_trans_date")
  private String sourceTransDate;

  @JsonProperty(value = "supplier_short_name")
  private String supplierShortname;

  @JsonProperty(value = "supplier_id")
  private String supplierId;

  @JsonProperty(value = "source_trans_id")
  private String sourceTransId;

  @JsonProperty(value = "case_open_date")
  private String caseOpenDate;

  @JsonProperty(value = "extended_unit_amt")
  private String extendedUnitAmt;

  @JsonProperty(value = "line_item_description")
  private String lineItemDescription;

  @JsonProperty(value = "product_category")
  private String productCategory;

  @JsonIgnore private String updatedBy;
  @UpdateTimestamp @JsonIgnore private LocalDateTime updatedDate;
}
