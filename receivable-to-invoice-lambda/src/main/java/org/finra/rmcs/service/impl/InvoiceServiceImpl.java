package org.finra.rmcs.service.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.entity.ReceivableEntity;
import org.finra.rmcs.entity.SalesItemEntity;
import org.finra.rmcs.repo.ReceivableRepo;
import org.finra.rmcs.service.InvoiceService;
import org.finra.rmcs.service.ReceivableRevenueStreamService;
import org.finra.rmcs.service.SalesItemService;
import org.finra.rmcs.utils.Utils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@AllArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

  private final ReceivableRepo receivableRepo;

  private final ReceivableRevenueStreamService receivableRevenueStreamService;
  private final SalesItemService salesItemService;

  private final Utils utils;

  @Override
  public void convertReceivableToInvoice(String correlationId) {
    String methodName =
        Constants.CLASS
            + this.getClass().getSimpleName()
            + " "
            + Constants.METHOD
            + Thread.currentThread().getStackTrace()[1].getMethodName()
            + " "
            + Constants.CORRELATION_ID
            + correlationId;

    log.info("{} message: method entry", methodName);

    List<ReceivableEntity> receivableList =
        receivableRepo.getReadyToBillReceivableOrderByCreatedDateDesc(
            receivableRevenueStreamService.isSendToWDEnabled());

    if (receivableList == null || CollectionUtils.isEmpty(receivableList)) {
      log.info("{} message:to create invoice there are no ready to bill records", methodName);
    } else {
      log.info("{} message: total {} ready to bill records", methodName, receivableList.size());
      List<String> revenueSteamList =
          receivableList.stream().map(ReceivableEntity::getRevenueStream).toList();
      List<SalesItemEntity> salesItemEntityList =
          salesItemService.getSalesItemList(revenueSteamList);
      List<ReceivableEntity> updatedReceivableList =
          receivableList.stream()
              .map(
                  receivableEntity -> utils.updateReceivable(receivableEntity, salesItemEntityList))
              .toList();
      try {
        if (!CollectionUtils.isEmpty(updatedReceivableList)) {
          receivableRepo.saveAll(updatedReceivableList);
          for (ReceivableEntity receivableEntity : updatedReceivableList) {
            log.info(
                Constants.REGULAR_EVENT_LOG_FORMAT,
                Constants.LAMBDA_NAME,
                methodName,
                Constants.EVENT_INVOICED_RECEIVABLE,
                correlationId,
                receivableEntity.getInvoiceId(),
                receivableEntity.getRevenueStream());
          }
          log.info(
              "{} message: total {} invoice records", methodName, updatedReceivableList.size());
        }
      } catch (Exception e) {
        for (ReceivableEntity receivableEntity : updatedReceivableList) {
          log.info(
              Constants.REGULAR_EVENT_LOG_FORMAT,
              Constants.LAMBDA_NAME,
              methodName,
              Constants.EVENT_ERROR_INVOICED_RECEIVABLE,
              correlationId,
              receivableEntity.getInvoiceId(),
              receivableEntity.getRevenueStream());
        }
        throw new RuntimeException(e);
      }
    }
  }
}
