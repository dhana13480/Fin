package org.finra.rmcs.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.dto.Receivable;
import org.finra.rmcs.dto.ReceivableItem;
import org.finra.rmcs.entity.ReceivableEntity;
import org.finra.rmcs.entity.ReceivableItemEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReceivableUtil {

  public Receivable convertReceivableEntityToDto(ReceivableEntity receivableEntity) {

    Receivable receivable = new Receivable();

    receivable.setId(receivableEntity.getId());
    receivable.setWdId(receivableEntity.getInvoiceId());
    receivable.setRevenueStreamReceivableId(receivableEntity.getRevenueStreamReceivableId());
    receivable.setCompany(receivableEntity.getCompany());
    receivable.setCustomerId(receivableEntity.getCustomerId());
    receivable.setRevenueStream(receivableEntity.getRevenueStream());
    receivable.setInvoiceId(receivableEntity.getInvoiceId());
    receivable.setTotalLine(receivableEntity.getTotalLine());
    receivable.setAmount(
        Objects.nonNull(receivableEntity.getAmount())
            ? receivableEntity.getAmount().setScale(2)
            : null);
    receivable.setClearingNumber(receivableEntity.getClearingNumber());
    receivable.setMpID(receivableEntity.getMpID());
    receivable.setExported(receivableEntity.getExported());
    receivable.setCreatedDate(convertToUtc(receivableEntity.getCreatedDate()));
    receivable.setLegacy(receivableEntity.isLegacy());
    receivable.setAccountingDate(receivableEntity.getAccountingDate());
    receivable.setInvoiceDate(convertToEST(receivableEntity.getInvoiceDate()));
    receivable.setInvoiceStatus(receivableEntity.getInvoiceStatus());
    return receivable;
  }

  public ReceivableItem convertReceivableItemEntityToDto(
      ReceivableItemEntity receivableItemEntity, String invoiceId) {
    ReceivableItem receivableItem = new ReceivableItem();
    receivableItem.setReceivableId(receivableItemEntity.getReceivableId());
    receivableItem.setSequence(receivableItemEntity.getSequence());
    receivableItem.setSourceId(invoiceId);

    receivableItem.setSalesItemId(receivableItemEntity.getSalesItemId());
    receivableItem.setSalesItemName(receivableItemEntity.getSalesItemName());
    receivableItem.setQuantity(receivableItemEntity.getQuantity());
    receivableItem.setUnitAmount(
        Objects.nonNull(receivableItemEntity.getUnitAmount())
            ? receivableItemEntity.getUnitAmount().setScale(6)
            : null);
    receivableItem.setAmount(
        Objects.nonNull(receivableItemEntity.getAmount())
            ? receivableItemEntity.getAmount().setScale(2)
            : null);
    receivableItem.setBranchId(
        StringUtils.isEmpty(receivableItemEntity.getBranchId())
            ? Constants.SPACE
            : receivableItemEntity.getBranchId());
    receivableItem.setFilingId(
        StringUtils.isEmpty(receivableItemEntity.getFilingId())
            ? Constants.SPACE
            : receivableItemEntity.getFilingId());
    receivableItem.setCrdTransactionDate(
        convertToEST(receivableItemEntity.getCrdTransactionDate()));
    receivableItem.setIndividualCrdNo(
        StringUtils.isEmpty(receivableItemEntity.getIndividualCrdNo())
            ? Constants.SPACE
            : receivableItemEntity.getIndividualCrdNo());
    receivableItem.setIndividualName(
        StringUtils.isEmpty(receivableItemEntity.getIndividualName())
            ? Constants.SPACE
            : receivableItemEntity.getIndividualName());
    receivableItem.setBillingCode(
        StringUtils.isEmpty(receivableItemEntity.getBillingCode())
            ? Constants.SPACE
            : receivableItemEntity.getBillingCode());
    receivableItem.setFromDate(convertToEST(receivableItemEntity.getFromDate()));
    receivableItem.setToDate(convertToEST(receivableItemEntity.getToDate()));
    receivableItem.setSourceTransDate(convertToEST(receivableItemEntity.getSourceTransDate()));
    receivableItem.setSourceTransId(
        StringUtils.isEmpty(receivableItemEntity.getSourceTransId())
            ? Constants.SPACE
            : receivableItemEntity.getSourceTransId());
    receivableItem.setCaseOpenDate(convertToEST(receivableItemEntity.getCaseOpenDate()));
    receivableItem.setLineItemDescription(receivableItemEntity.getLineItemDescription());
    receivableItem.setProductCategory(
        StringUtils.isEmpty(receivableItemEntity.getProductCategory())
            ? Constants.SPACE
            : receivableItemEntity.getProductCategory());
    receivableItem.setTransactionDate(receivableItemEntity.getTransactionDate());
    receivableItem.setExtendedUnitAmt(receivableItemEntity.getExtendedUnitAmt());
    receivableItem.setSalesTax(receivableItemEntity.getSalesTax());
    receivableItem.setTotal(
        Objects.nonNull(receivableItemEntity.getTotal())
            ? receivableItemEntity.getTotal().setScale(2)
            : null);
    return receivableItem;
  }

  private LocalDateTime convertToUtc(LocalDateTime dateTime) {
    if (Objects.isNull(dateTime)) {
      return null;
    }
    ZonedDateTime dateTimeInMyZone = ZonedDateTime.of(dateTime, ZoneId.systemDefault());

    return dateTimeInMyZone.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
  }

  private String convertToEST(ZonedDateTime zonedDateTime) {
    if (Objects.isNull(zonedDateTime)) {
      return null;
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00");
    return zonedDateTime
        .withZoneSameInstant(ZoneId.of("America/New_York"))
        .toLocalDate()
        .format(formatter);
  }

  public List<String> getJsonLines(List<Receivable> wdReceivables) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    List<String> jsonLines =
        wdReceivables.parallelStream()
            .map(
                wdReceivable -> {
                  try {
                    return objectMapper.writeValueAsString(wdReceivable);
                  } catch (JsonProcessingException e) {
                    log.error(
                        Constants.JSON_LINES_ERROR,
                        wdReceivable.getId(),
                        e.getMessage(),
                        e.toString());
                    return Constants.BLANK_STRING;
                  }
                })
            .collect(Collectors.toList());
    jsonLines.removeIf(String::isEmpty);

    String statsLine = String.format(Constants.STATS_JSON, jsonLines.size());
    jsonLines.add(statsLine);
    Collections.reverse(jsonLines);
    return jsonLines;
  }
}
