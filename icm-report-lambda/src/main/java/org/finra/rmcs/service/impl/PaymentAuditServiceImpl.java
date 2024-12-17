package org.finra.rmcs.service.impl;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.constants.ReceivableAuditConstants;
import org.finra.rmcs.dto.PaymentReport;
import org.finra.rmcs.entity.PaymentEntity;
import org.finra.rmcs.entity.PaymentStatusHistory;
import org.finra.rmcs.repo.PaymentRepo;
import org.finra.rmcs.repo.PaymentStatusHistoryRepo;
import org.finra.rmcs.service.PaymentAuditService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class PaymentAuditServiceImpl implements PaymentAuditService {

  private final PaymentStatusHistoryRepo paymentStatusHistoryRepo;
  private final PaymentRepo paymentRepo;

  @Override
  public List<PaymentReport> findByCreatedDate(ZonedDateTime entryDate) {
    String startDate =
        new StringBuilder()
            .append("'")
            .append(entryDate.toLocalDate())
            .append(" 00:00:00 ")
            .append(entryDate.getOffset())
            .append("'")
            .toString();
    String endDate =
        new StringBuilder()
            .append("'")
            .append(entryDate.toLocalDate())
            .append(" 23:59:59 ")
            .append(entryDate.getOffset())
            .append("'")
            .toString();
    log.info(
        "SQL Query: SELECT * FROM payments.payment_status_history WHERE workday_notify_status_id = 2 AND created_timestamp between {}} and {}",
        startDate,
        endDate);
    List<PaymentStatusHistory> paymentStatusHistories =
        paymentStatusHistoryRepo.findStatusHistoryByDate(startDate, endDate);
    List<UUID> ids =
        paymentStatusHistories.stream().map(PaymentStatusHistory::getPaymentId).toList();
    log.info("SQL Query: SELECT * FROM payments.payment WHERE id in {}", ids);
    List<PaymentEntity> paymentEntities = paymentRepo.findByIds(ids);
    log.info("payment size: {}", paymentEntities.size());
    log.info(
        "payment_id(s): {}",
        paymentEntities.stream().map(paymentEntity -> paymentEntity.getId().toString()).toList());

    Map<String, List<PaymentEntity>> paymentEntityMap =
        paymentEntities.stream()
            .collect(Collectors.groupingBy(PaymentEntity::getRevenueStream, Collectors.toList()));

    List<PaymentReport> paymentReports = new ArrayList<>();
    paymentEntityMap
        .entrySet()
        .forEach(
            entry -> {
              String revenueStream = entry.getKey();
              List<PaymentEntity> paymentEntityList = entry.getValue();
              PaymentReport amount = new PaymentReport();
              amount.setRevenueStream(revenueStream);
              String amountValue = ReceivableAuditConstants.ZERO;
              for (PaymentEntity paymentEntity : paymentEntityList) {
                amountValue =
                    calculateSumAmount(amountValue, paymentEntity.getTotalAmount().toString());
              }
              amount.setCheck(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_AMOUNT);
              amount.setSourceData(amountValue);

              PaymentReport check = new PaymentReport();
              check.setRevenueStream(revenueStream);
              check.setCheck(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_COUNT);
              check.setSourceData(String.valueOf(paymentEntityList.size()));

              paymentReports.add(amount);
              paymentReports.add(check);
            });
    return paymentReports;
  }

  private String calculateSumAmount(String amount1, String amount2) {
    return String.valueOf(new BigDecimal(amount1).add(new BigDecimal(amount2)).setScale(2));
  }
}
