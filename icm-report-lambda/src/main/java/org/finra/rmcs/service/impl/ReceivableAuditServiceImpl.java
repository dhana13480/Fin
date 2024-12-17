package org.finra.rmcs.service.impl;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.finra.rmcs.common.constants.ReceivableStatus;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.dto.ReceivableAudit;
import org.finra.rmcs.entity.ReceivableAuditEntity;
import org.finra.rmcs.entity.RevenueStreamEntity;
import org.finra.rmcs.repo.ReceivableAuditRepo;
import org.finra.rmcs.repo.ReceivableRevenueStreamRepo;
import org.finra.rmcs.service.ReceivableAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ReceivableAuditServiceImpl implements ReceivableAuditService {

  private final ReceivableAuditRepo receivableAuditRepo;

  private final ReceivableRevenueStreamRepo receivableRevenueStreamRepo;

  private Map<String, Boolean> lookupPaymentValidation;

  @PostConstruct
  public void init() {
    List<RevenueStreamEntity> revenueStreamEntities = receivableRevenueStreamRepo.findAll();
      lookupPaymentValidation =
        revenueStreamEntities.stream()
            .collect(
                Collectors.toMap(
                    revenueStreamEntity -> revenueStreamEntity.getRevenueStreamName().trim(),
                   RevenueStreamEntity::isPaymentValidation)
            );
  }

  @Override
  public List<ReceivableAudit> findByAuditEntryCreatedDate(ZonedDateTime entryDate) {
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
        "SQL Query: SELECT * FROM receivable_audit ra where ra.audit_entry_created_date {} and {} order by ra.audit_entry_created_date asc",
        startDate,
        endDate);
    List<ReceivableAuditEntity> receivableAuditEntityList =
        receivableAuditRepo.findByAuditEntryCreatedDate(startDate, endDate);
    receivableAuditEntityList =
        receivableAuditEntityList.stream()
            .filter(
                receivableAuditEntity ->
                    Objects.equals(
                        receivableAuditEntity.getStatus(), ReceivableStatus.SENT_TO_WD.getId()))
            .toList();
    receivableAuditEntityList = filterEligibleList(receivableAuditEntityList);
    log.info("invoice size: {}", receivableAuditEntityList.size());
    log.info(
        "invoice_id(s): {}",
        receivableAuditEntityList.stream().map(ReceivableAuditEntity::getInvoiceId).toList());
    return this.BuildReceivableAuditData(receivableAuditEntityList);
  }

  private List<ReceivableAudit> BuildReceivableAuditData(
      List<ReceivableAuditEntity> receivableAuditEntityList) {
    List<ReceivableAudit> receivableAuditList =
        receivableAuditEntityList.stream()
            .map(
                entity -> {
                  ReceivableAudit receivableAudit = new ReceivableAudit();
                  receivableAudit.setId(entity.getId());
                  receivableAudit.setBalance(entity.getBalance());
                  receivableAudit.setAmount(String.valueOf(deriveAmount(entity.getAmount())));
                  receivableAudit.setAction(entity.getAction());
                  receivableAudit.setAuditEntryCreatedDate(entity.getAuditEntryCreatedDate());
                  receivableAudit.setCompany(entity.getCompany());
                  receivableAudit.setExported(entity.getExported());
                  receivableAudit.setClearingNumber(entity.getClearingNumber());
                  receivableAudit.setCreatedBy(entity.getCreatedBy());
                  receivableAudit.setRevenueStreamReceivableId(
                      entity.getRevenueStreamReceivableId());
                  receivableAudit.setReceivableEntryId(entity.getReceivableEntryId());
                  receivableAudit.setCreatedDate(entity.getCreatedDate());
                  receivableAudit.setCreatedBy(entity.getCreatedBy());
                  receivableAudit.setUpdatedDate(entity.getUpdatedDate());
                  receivableAudit.setTransmissionId(entity.getTransmissionId());
                  receivableAudit.setTransactionDate(
                      (entity.getTransactionDate() == null)
                          ? ""
                          : entity.getTransactionDate().toString());
                  receivableAudit.setSource(entity.getSource());
                  receivableAudit.setStatusReason(entity.getStatusReason());
                  receivableAudit.setStatus(entity.getStatus());
                  receivableAudit.setRevenueStream(entity.getRevenueStream());
                  receivableAudit.setInvoiceStatus(entity.getInvoiceStatus());
                  receivableAudit.setUpdatedBy(entity.getUpdatedBy());
                  receivableAudit.setCustomerId(entity.getCustomerId());
                  receivableAudit.setDueDate(entity.getDueDate());
                  receivableAudit.setCreatedDate(entity.getCreatedDate());
                  receivableAudit.setInvoiceId(entity.getInvoiceId());
                  receivableAudit.setInvoiceType(entity.getInvoiceType());
                  receivableAudit.setInvoiceDate(entity.getInvoiceDate());
                  receivableAudit.setLegacy(entity.isLegacy());
                  receivableAudit.setTotalLines(String.valueOf(entity.getTotalLine()));
                  receivableAudit.setMpid(entity.getMpID());
                  receivableAudit.setProcessingRevenueStream(entity.getProcessingRevenueStream());
                  receivableAudit.setRevenueStream(entity.getRevenueStream());
                  receivableAudit.setStatusReason(entity.getStatusReason());
                  return receivableAudit;
                })
            .collect(Collectors.toList());

    return receivableAuditList;
  }

  private BigDecimal deriveAmount(BigDecimal amount) {
    return amount == null ? BigDecimal.ZERO : amount;
  }

  private List<ReceivableAuditEntity> filterEligibleList(
      List<ReceivableAuditEntity> receivableAuditEntityList) {
    // same day scenario
    // having two entries with status 16 and same invoice id in the same day, only one should be
    // included in ICM report.

    // different day scenario
    // some revenue streams with payment_validation false, which is not required payment_received =
    // Y to send to Workday,
    // then if we received the payment in the next day, then that entry should not be included in
    // ICM report as well as this invoice has been sent at the day 1
    List<ReceivableAuditEntity> receivableAuditEntitiesAfterDistinct =
        receivableAuditEntityList.stream().distinct().toList();

    log.info(
        "removed {} from list since it is not eligible for same day scenario.",
        CollectionUtils.subtract(
            receivableAuditEntityList.stream().map(ReceivableAuditEntity::getInvoiceId).toList(),
            receivableAuditEntitiesAfterDistinct.stream()
                .map(ReceivableAuditEntity::getInvoiceId)
                .toList()));

    List<ReceivableAuditEntity> eligibleList = new ArrayList<>();
    for (ReceivableAuditEntity receivableAuditEntity : receivableAuditEntitiesAfterDistinct) {
      LocalDate auditEntryDate = convertToETDate(receivableAuditEntity.getAuditEntryCreatedDate());
      LocalDate createdDate = convertToETDate(receivableAuditEntity.getCreatedDate());
      if (auditEntryDate.isEqual(createdDate)) {
        eligibleList.add(receivableAuditEntity);
      } else {
        if ((lookupPaymentValidation.get(receivableAuditEntity.getRevenueStream())
                && "Y".equalsIgnoreCase(receivableAuditEntity.getPaymentReceived()))
            || (!lookupPaymentValidation.get(receivableAuditEntity.getRevenueStream())
                && !Constants.EXCEPTION_LIST.contains(receivableAuditEntity.getRevenueStream()))
            || (!lookupPaymentValidation.get(receivableAuditEntity.getRevenueStream())
                && auditEntryDate.getDayOfWeek() == DayOfWeek.SATURDAY)) {
          eligibleList.add(receivableAuditEntity);
        } else {
          log.info(
              "removed {} from list since it is not eligible for different day scenario.",
              receivableAuditEntity.getInvoiceId());
        }
      }
    }
    return eligibleList;
  }

  private LocalDate convertToETDate(ZonedDateTime zonedDateTime) {
    if (Objects.isNull(zonedDateTime)) {
      return null;
    }
    return zonedDateTime.withZoneSameInstant(ZoneId.of("America/New_York")).toLocalDate();
  }
}
