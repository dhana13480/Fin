package org.finra.rmcs.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.common.constants.ReceivableStatus;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.entity.ReceivableEntity;
import org.finra.rmcs.entity.ReceivableItemEntity;
import org.finra.rmcs.entity.SalesItemEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class Utils {

  public ReceivableEntity updateReceivable(
      ReceivableEntity receivableEntity, List<SalesItemEntity> salesItemEntityList) {
    receivableEntity.setUpdatedBy(Constants.UPDATED_BY);
    receivableEntity.setUpdatedDate(LocalDateTime.now());
    receivableEntity.setInvoiceStatus(Constants.INVOICE_STATUS);
    receivableEntity.setInvoiceDate(LocalDateTime.now());
    receivableEntity.setClearingNumber(
        StringUtils.isBlank(receivableEntity.getClearingNumber())
            ? StringUtils.EMPTY
            : receivableEntity.getClearingNumber());
    receivableEntity.setMpID(
        StringUtils.isBlank(receivableEntity.getMpID())
            ? StringUtils.EMPTY
            : receivableEntity.getMpID());
    receivableEntity.setStatus(ReceivableStatus.getNextStatusId(receivableEntity.getStatus()));
    List<ReceivableItemEntity> receivableItems =
        receivableEntity.getReceivableItems().stream().map(this::updateReceivableItem).toList();
    receivableEntity.setReceivableItems(receivableItems);
    updateToAndFromDate(receivableEntity, salesItemEntityList);
    return receivableEntity;
  }

  public ReceivableItemEntity updateReceivableItem(ReceivableItemEntity receivableItem) {
    receivableItem.setUpdatedBy(Constants.UPDATED_BY);
    receivableItem.setUpdatedDate(LocalDateTime.now());
    receivableItem.setSalesTax(new BigDecimal(0));
    receivableItem.setTotal(receivableItem.getSalesTax().add(receivableItem.getAmount()));
    receivableItem.setBranchId(
        StringUtils.isBlank(receivableItem.getBranchId())
            ? StringUtils.EMPTY
            : receivableItem.getBranchId());
    receivableItem.setFilingId(
        StringUtils.isBlank(receivableItem.getFilingId())
            ? StringUtils.EMPTY
            : receivableItem.getFilingId());
    receivableItem.setIndividualCrdNo(
        StringUtils.isBlank(receivableItem.getIndividualCrdNo())
            ? StringUtils.EMPTY
            : receivableItem.getIndividualCrdNo());
    receivableItem.setIndividualName(
        StringUtils.isBlank(receivableItem.getIndividualName())
            ? StringUtils.EMPTY
            : receivableItem.getIndividualName());
    receivableItem.setBillingCode(
        StringUtils.isBlank(receivableItem.getBillingCode())
            ? StringUtils.EMPTY
            : receivableItem.getBillingCode());
    receivableItem.setSupplierInvoiceNo(
        StringUtils.isBlank(receivableItem.getSupplierInvoiceNo())
            ? StringUtils.EMPTY
            : receivableItem.getSupplierInvoiceNo());
    receivableItem.setSupplierShortname(
        StringUtils.isBlank(receivableItem.getSupplierShortname())
            ? StringUtils.EMPTY
            : receivableItem.getSupplierShortname());
    receivableItem.setSupplierId(
        StringUtils.isBlank(receivableItem.getSupplierId())
            ? StringUtils.EMPTY
            : receivableItem.getSupplierId());
    receivableItem.setSourceTransId(
        StringUtils.isBlank(receivableItem.getSourceTransId())
            ? StringUtils.EMPTY
            : receivableItem.getSourceTransId());
    receivableItem.setLineItemDescription(
        StringUtils.isBlank(receivableItem.getLineItemDescription())
            ? StringUtils.EMPTY
            : receivableItem.getLineItemDescription());
    receivableItem.setProductCategory(
        StringUtils.isBlank(receivableItem.getProductCategory())
            ? StringUtils.EMPTY
            : receivableItem.getProductCategory());
    receivableItem.setSourceTransId(
        StringUtils.isNotBlank(receivableItem.getSourceTransId())
            ? receivableItem.getSourceTransId()
            : receivableItem.getId().toString());
    return receivableItem;
  }

  private void updateToAndFromDate(
      ReceivableEntity receivableEntity, List<SalesItemEntity> salesItemEntityList) {
    Optional<SalesItemEntity> optional = null;
    Map<String, String> map = new HashMap<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    for (ReceivableItemEntity item : receivableEntity.getReceivableItems()) {
      optional =
          salesItemEntityList.stream()
              .filter(
                  entity ->
                      entity
                          .getRevenueStream()
                          .equalsIgnoreCase(receivableEntity.getRevenueStream()))
              .filter(entity -> entity.getSalesItemId().equalsIgnoreCase(item.getSalesItemId()))
              .findFirst();
      String defRevRuleId = StringUtils.EMPTY;
      if (optional.isPresent() && StringUtils.isNotBlank(optional.get().getDefRevRuleId())) {
        defRevRuleId = optional.get().getDefRevRuleId();
      }
      if (DeferredRevenueUtil.isCaseOpenDate(defRevRuleId)) {
        if (item.getCaseOpenDate() != null) {
          map = DeferredRevenueUtil.deferredRevenueRulesCalc(item.getCaseOpenDate(), defRevRuleId);
        }
      } else {
        map =
            DeferredRevenueUtil.deferredRevenueRulesCalc(
                receivableEntity.getInvoiceDate(), defRevRuleId);
      }
      if (map.get(Constants.FROM_DATE) != null) {
        LocalDate fromLd = LocalDate.parse(map.get(Constants.FROM_DATE), formatter);
        ZonedDateTime zdt = fromLd.atStartOfDay(ZoneId.of(Constants.TIME_ZONE_AMERICA_NEW_YORK));
        item.setFromDate(zdt);
      }
      if (map.get(Constants.TO_DATE) != null) {
        LocalDate toLd = LocalDate.parse(map.get(Constants.TO_DATE), formatter);
        ZonedDateTime zdt = toLd.atStartOfDay(ZoneId.of(Constants.TIME_ZONE_AMERICA_NEW_YORK));
        item.setToDate(zdt);
      }
    }
  }
}
