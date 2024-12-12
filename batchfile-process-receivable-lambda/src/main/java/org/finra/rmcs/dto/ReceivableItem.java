package org.finra.rmcs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceivableItem {

  @NotBlank(message = "sales_item_id is required")
  @JsonProperty("sales_item_id")
  private String salesItemId;

  @JsonProperty("create_timestamp")
  private String transactionDate;

  @NotNull(message = "sequence is required")
  private String sequence;

  @JsonProperty("sales_items_name")
  private String salesItemsName;

  @JsonProperty("source_id")
  private String sourceId;

  @JsonProperty("quantity")
  private String quantity;

  @JsonProperty("unit_amt")
  private String unitAmt;

  @JsonProperty("amount")
  private String amount;

  @JsonProperty("crd_transaction_date")
  private String crdTransactionDate;

  @JsonProperty("branch_id")
  private String branchId;

  @JsonProperty("filing_id")
  private String filingId;

  @JsonProperty("individual_crd_no")
  private String individualCrdNo;

  @JsonProperty("individual_name")
  private String individualName;

  @JsonProperty("billing_code")
  private String billingCode;

  @JsonProperty("from_date")
  private String fromDate;

  @JsonProperty("to_date")
  private String toDate;

  @JsonProperty("supplier_invoice_no")
  private String supplierInvoiceNo;

  @JsonProperty("source_trans_date")
  private String sourceTransDate;

  @JsonProperty("supplier_short_name")
  private String supplierShortName;

  @JsonProperty("supplier_id")
  private String supplierId;

  @JsonProperty("source_trans_id")
  private String sourceTransId;

  @JsonProperty("case_open_date")
  private String caseOpenDate;

  @JsonProperty("line_item_description")
  private String lineItemDescription;

  @JsonProperty("product_category")
  private String productCategory;
}
