package org.finra.rmcs.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.common.constants.ReceivableStatus;
import org.finra.rmcs.entity.ReceivableEntity;
import org.finra.rmcs.entity.ReceivableItemEntity;
import org.finra.rmcs.entity.SalesItemEntity;
import org.finra.rmcs.repo.ReceivableRepo;
import org.finra.rmcs.repo.SalesItemRepo;
import org.finra.rmcs.service.ReceivableService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ReceivableServiceImpl implements ReceivableService {
  private final ReceivableRepo receivableRepo;
  private final SalesItemRepo salesItemRepo;
  private static Map<String, String> salesItemNameMap = new HashMap<>();

  public List<ReceivableEntity> findValidReceivablesByInvoiceIds(List<String> invoiceIds) {
    log.info(
        "Get-Receivable-lambda: retreiving valid receivables for invoice id(s): {}", invoiceIds);

    List<ReceivableEntity> receivableEntities =
        receivableRepo.findValidReceivablesByInvoiceIds(
            invoiceIds, ReceivableStatus.INVALID.getId(), ReceivableStatus.SUSPEND.getId());
    for (ReceivableEntity receivableEntity : receivableEntities) {
      String revenueStream =
          StringUtils.isNotBlank(receivableEntity.getProcessingRevenueStream())
              ? receivableEntity.getProcessingRevenueStream()
              : receivableEntity.getRevenueStream();
      for(ReceivableItemEntity receivableItem : receivableEntity.getReceivableItems()){
        receivableItem.setSalesItemNameDesc(getSalesItemNameDesc(receivableItem, revenueStream));
      }
    }

    return receivableEntities;
  }

  private String getSalesItemNameDesc(ReceivableItemEntity receivableItem, String revenueStream) {
    String key = revenueStream + receivableItem.getSalesItemId();

    if (salesItemNameMap.containsKey(key)) {
      return salesItemNameMap.get(key);
    } else {
      SalesItemEntity sie =
          salesItemRepo.findBySalesItemAndRevenueStream(receivableItem.getSalesItemId(), revenueStream);
      if (sie != null) {
        salesItemNameMap.put(key, sie.getSalesItemNameDesc());
        return sie.getSalesItemNameDesc();
      }
      return null;
    }
  }
}
