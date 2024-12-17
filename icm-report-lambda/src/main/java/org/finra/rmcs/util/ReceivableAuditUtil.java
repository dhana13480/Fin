package org.finra.rmcs.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.common.constants.ReceivableStatus;
import org.finra.rmcs.constants.ReceivableAuditConstants;
import org.finra.rmcs.dto.ReceivableAudit;
import org.finra.rmcs.dto.ReceivableReport;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReceivableAuditUtil {
  public List<ReceivableReport> getRMCSInboundList(List<ReceivableAudit> receivableAuditList) {
    List<ReceivableAudit> rmcsInboundList =
        receivableAuditList.stream()
            .filter(
                s ->
                    s.getStatus() == ReceivableStatus.INVALID.getId()
                        || s.getStatus() == ReceivableStatus.READY_TO_BILL.getId())
            .collect(Collectors.toList());
    Map<String, ReceivableReport> reportMap = new HashMap<>();
    Map<String, ReceivableAudit> auditList = new HashMap<>();

    deriveInboundList(rmcsInboundList, auditList, reportMap);

    auditList.forEach(
        (key, item) -> {
          ReceivableReport tempAmount =
              reportMap.get(
                  new StringBuilder()
                      .append(item.getRevenueStream())
                      .append(ReceivableAuditConstants.HYPHEN)
                      .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_AMOUNT)
                      .toString());
          ReceivableReport tempCount =
              reportMap.get(
                  new StringBuilder()
                      .append(item.getRevenueStream())
                      .append(ReceivableAuditConstants.HYPHEN)
                      .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_COUNT)
                      .toString());
          tempAmount.setSourceData(
              calculateSumAmount(item.getAmount(), tempAmount.getSourceData()));
          tempCount.setSourceData(calculateSumCount(ReceivableAuditConstants.ONE, tempCount.getSourceData()));
          if (item.getStatus() == ReceivableStatus.READY_TO_BILL.getId()) {
            tempAmount.setDestinationData(
                calculateSumAmount(item.getAmount(), tempAmount.getDestinationData()));
            tempCount.setDestinationData(calculateSumCount(ReceivableAuditConstants.ONE, tempCount.getDestinationData()));
          }
          if (item.getStatus() == ReceivableStatus.INVALID.getId()) {
            tempAmount.setRejected(calculateSumAmount(item.getAmount(), tempAmount.getRejected()));
            tempCount.setRejected(calculateSumCount(ReceivableAuditConstants.ONE, tempCount.getRejected()));
          }
          reportMap.replace(
              new StringBuilder()
                  .append(item.getRevenueStream())
                  .append(ReceivableAuditConstants.HYPHEN)
                  .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_AMOUNT)
                  .toString(),
              tempAmount);
          reportMap.replace(
              new StringBuilder()
                  .append(item.getRevenueStream())
                  .append(ReceivableAuditConstants.HYPHEN)
                  .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_COUNT)
                  .toString(),
              tempCount);
        });

    deriveReportMap(reportMap);

    List<ReceivableReport> receivableReports =
        reportMap.values().stream().collect(Collectors.toList());

    return receivableReports;
  }

  private void deriveReportMap(Map<String, ReceivableReport> reportMap) {
    reportMap
        .values()
        .iterator()
        .forEachRemaining(
            item -> {
              if (item.getCheck()
                  .equalsIgnoreCase(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_COUNT)) {
                item.setDifference(
                    calculateDiffCount(
                        item.getSourceData(), item.getDestinationData(), item.getRejected()));
                if (Integer.valueOf(item.getDifference()) == Integer.valueOf(ReceivableAuditConstants.ZERO)) {
                  item.setResult(ReceivableAuditConstants.SUCCESS);
                } else {
                  item.setResult(ReceivableAuditConstants.FAILURE);
                }
              } else {
                item.setDifference(
                    calculateDiffAmount(
                        item.getSourceData(), item.getDestinationData(), item.getRejected()));
                if (new BigDecimal(item.getDifference()).compareTo(BigDecimal.ZERO) == 0) {
                  item.setResult(ReceivableAuditConstants.SUCCESS);
                } else {
                  item.setResult(ReceivableAuditConstants.FAILURE);
                }
              }
            });
  }

  private void deriveInboundList(
      List<ReceivableAudit> rmcsInboundList,
      Map<String, ReceivableAudit> auditList,
      Map<String, ReceivableReport> reportMap) {
    rmcsInboundList.forEach(
        item -> {
          if ((item.getStatus() == ReceivableStatus.INVALID.getId()
                  && auditList.containsKey(item.getInvoiceId() + ReceivableStatus.INVALID.getId()))
              || (item.getStatus() == ReceivableStatus.READY_TO_BILL.getId()
                  && (auditList.containsKey(item.getInvoiceId() + ReceivableStatus.INVALID.getId())
                      || auditList.containsKey(
                          item.getInvoiceId() + ReceivableStatus.READY_TO_BILL.getId())))) {
            ReceivableAudit temp;
            if (auditList.containsKey(item.getInvoiceId() + ReceivableStatus.INVALID.getId())) {
              temp = auditList.get(item.getInvoiceId() + ReceivableStatus.INVALID.getId());
            } else {
              temp = auditList.get(item.getInvoiceId() + ReceivableStatus.READY_TO_BILL.getId());
            }
            if (temp.getAuditEntryCreatedDate().isBefore(item.getAuditEntryCreatedDate())) {
              auditList.remove(temp.getInvoiceId() + temp.getStatus());
              auditList.put(item.getInvoiceId() + item.getStatus(), item);
            }
          } else if (!auditList.containsKey(
              item.getInvoiceId() + ReceivableStatus.READY_TO_BILL.getId())) {
            auditList.put(item.getInvoiceId() + item.getStatus(), item);
          }
          if (reportMap != null && !reportMap.containsKey(item.getRevenueStream())) {
            reportMap.put(
                new StringBuilder()
                    .append(item.getRevenueStream())
                    .append(ReceivableAuditConstants.HYPHEN)
                    .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_AMOUNT)
                    .toString(),
                ReceivableReport.builder()
                    .revenueStream(item.getRevenueStream())
                    .check(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_AMOUNT)
                    .sourceData(ReceivableAuditConstants.ZERO)
                    .destinationData(ReceivableAuditConstants.ZERO)
                    .rejected(ReceivableAuditConstants.ZERO)
                    .build());
            reportMap.put(
                new StringBuilder()
                    .append(item.getRevenueStream())
                    .append(ReceivableAuditConstants.HYPHEN)
                    .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_COUNT)
                    .toString(),
                ReceivableReport.builder()
                    .revenueStream(item.getRevenueStream())
                    .sourceData(ReceivableAuditConstants.ZERO)
                    .destinationData(ReceivableAuditConstants.ZERO)
                    .rejected(ReceivableAuditConstants.ZERO)
                    .check(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_COUNT)
                    .build());
          }
        });
  }

  private String calculateSumAmount(String amount1, String amount2) {
    return String.valueOf(new BigDecimal(amount1).add(new BigDecimal(amount2)).setScale(2));
  }

  private String calculateSumCount(String amount1, String amount2) {
    return String.valueOf(Integer.parseInt(amount1) + Integer.valueOf(amount2));
  }

  private String calculateDiffAmount(
      String sourceData, String destinationData, String rejectedData) {
    return String.valueOf(
        new BigDecimal(sourceData)
            .subtract(new BigDecimal(destinationData))
            .subtract(new BigDecimal(rejectedData))
            .setScale(2));
  }

  private String calculateDiffCount(
      String sourceData, String destinationData, String rejectedData) {
    return String.valueOf(
        Integer.parseInt(sourceData)
            - Integer.valueOf(destinationData)
            - Integer.valueOf(rejectedData));
  }

  public List<ReceivableAudit> getRMCSInboundSubList(List<ReceivableAudit> receivableAuditList) {
    List<ReceivableAudit> rmcsInboundSubList =
        receivableAuditList.stream()
            .filter(
                s ->
                    s.getStatus() == ReceivableStatus.INVALID.getId()
                        || s.getStatus() == ReceivableStatus.READY_TO_BILL.getId())
            .collect(Collectors.toList());

    Map<String, ReceivableAudit> auditList = new HashMap<>();

    deriveInboundList(rmcsInboundSubList, auditList, null);

    List<ReceivableAudit> receivableReports =
        auditList.values().stream()
            .filter(s -> s.getStatus() == ReceivableStatus.INVALID.getId())
            .collect(Collectors.toList());

    return receivableReports;
  }

  public List<ReceivableReport> getRMCSProcessList(List<ReceivableAudit> receivableAuditList) {
    List<ReceivableAudit> rmcsProcessList =
        receivableAuditList.stream()
            .filter(
                s ->
                    s.getStatus() == ReceivableStatus.READY_TO_BILL.getId()
                        || s.getStatus() == ReceivableStatus.INVOICED.getId())
            .collect(Collectors.toList());
    Map<String, ReceivableReport> reportMap = new HashMap<>();
    Map<String, ReceivableAudit> auditList = new HashMap<>();

    deriveProcessList(rmcsProcessList, auditList, reportMap);

    auditList.forEach(
        (key, item) -> {
          ReceivableReport tempAmount =
              reportMap.get(
                  new StringBuilder()
                      .append(item.getRevenueStream())
                      .append(ReceivableAuditConstants.HYPHEN)
                      .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_AMOUNT)
                      .toString());
          ReceivableReport tempCount =
              reportMap.get(
                  new StringBuilder()
                      .append(item.getRevenueStream())
                      .append(ReceivableAuditConstants.HYPHEN)
                      .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_COUNT)
                      .toString());

          if (item.getStatus() == ReceivableStatus.READY_TO_BILL.getId()) {
            tempAmount.setSourceData(
                calculateSumAmount(item.getAmount(), tempAmount.getSourceData()));
            tempCount.setSourceData(calculateSumCount(ReceivableAuditConstants.ONE, tempCount.getSourceData()));
            if (!auditList.containsKey(item.getInvoiceId() + ReceivableStatus.INVOICED.getId())) {
              tempAmount.setRejected(
                  calculateSumAmount(item.getAmount(), tempAmount.getRejected()));
              tempCount.setRejected(calculateSumCount(ReceivableAuditConstants.ONE, tempCount.getRejected()));
            }
          }
          if (item.getStatus() == ReceivableStatus.INVOICED.getId()) {
            tempAmount.setDestinationData(
                calculateSumAmount(item.getAmount(), tempAmount.getDestinationData()));
            tempCount.setDestinationData(calculateSumCount(ReceivableAuditConstants.ONE, tempCount.getDestinationData()));
          }
          reportMap.replace(
              new StringBuilder()
                  .append(item.getRevenueStream())
                  .append(ReceivableAuditConstants.HYPHEN)
                  .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_AMOUNT)
                  .toString(),
              tempAmount);
          reportMap.replace(
              new StringBuilder()
                  .append(item.getRevenueStream())
                  .append(ReceivableAuditConstants.HYPHEN)
                  .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_COUNT)
                  .toString(),
              tempCount);
        });
    deriveReportMap(reportMap);
    List<ReceivableReport> receivableReports =
        reportMap.values().stream().collect(Collectors.toList());

    return receivableReports;
  }

  public List<ReceivableAudit> getRMCSProcessSubList(List<ReceivableAudit> receivableAuditList) {
    List<ReceivableAudit> rmcsProcessSubList =
        receivableAuditList.stream()
            .filter(
                s ->
                    s.getStatus() == ReceivableStatus.READY_TO_BILL.getId()
                        || s.getStatus() == ReceivableStatus.INVOICED.getId())
            .collect(Collectors.toList());
    Map<String, ReceivableAudit> auditList = new HashMap<>();

    deriveProcessList(rmcsProcessSubList, auditList, null);
    List<String> unprocessedListKeys = new ArrayList<>();

    auditList.forEach(
        (key, value) -> {
          if (value.getStatus() == ReceivableStatus.READY_TO_BILL.getId()
              && !auditList.containsKey(value.getInvoiceId() + ReceivableStatus.INVOICED.getId())) {
            unprocessedListKeys.add(key);
          }
        });

    List<ReceivableAudit> receivableReports =
        auditList.values().stream()
            .filter(
                s ->
                    unprocessedListKeys.contains(
                        s.getInvoiceId() + ReceivableStatus.READY_TO_BILL.getId()))
            .collect(Collectors.toList());

    return receivableReports;
  }

  private void deriveProcessList(
      List<ReceivableAudit> rmcsProcessList,
      Map<String, ReceivableAudit> auditList,
      Map<String, ReceivableReport> reportMap) {
    rmcsProcessList.forEach(
        item -> {
          if (auditList.containsKey(item.getInvoiceId() + item.getStatus())) {
            ReceivableAudit temp = auditList.get(item.getInvoiceId() + item.getStatus());
            if (temp.getAuditEntryCreatedDate().isBefore(item.getAuditEntryCreatedDate())) {
              auditList.replace(item.getInvoiceId() + item.getStatus(), item);
            }
          } else {
            auditList.put(item.getInvoiceId() + item.getStatus(), item);
          }
          if (reportMap != null && !reportMap.containsKey(item.getRevenueStream())) {
            reportMap.put(
                new StringBuilder()
                    .append(item.getRevenueStream())
                    .append(ReceivableAuditConstants.HYPHEN)
                    .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_AMOUNT)
                    .toString(),
                ReceivableReport.builder()
                    .revenueStream(item.getRevenueStream())
                    .check(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_AMOUNT)
                    .sourceData(ReceivableAuditConstants.ZERO)
                    .destinationData(ReceivableAuditConstants.ZERO)
                    .rejected(ReceivableAuditConstants.ZERO)
                    .build());
            reportMap.put(
                new StringBuilder()
                    .append(item.getRevenueStream())
                    .append(ReceivableAuditConstants.HYPHEN)
                    .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_COUNT)
                    .toString(),
                ReceivableReport.builder()
                    .revenueStream(item.getRevenueStream())
                    .sourceData(ReceivableAuditConstants.ZERO)
                    .destinationData(ReceivableAuditConstants.ZERO)
                    .rejected(ReceivableAuditConstants.ZERO)
                    .check(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_COUNT)
                    .build());
          }
        });
  }

  public List<ReceivableReport> getRMCSOutboundList(List<ReceivableAudit> receivableAuditList) {
    List<ReceivableAudit> rmcsOutboundList =
        receivableAuditList.stream()
            .filter(
                s -> s.getStatus() == ReceivableStatus.SENT_TO_WD.getId())
            .toList();

    Map<String, ReceivableReport> reportMap = new HashMap<>();
    Map<String, ReceivableAudit> auditList = new HashMap<>();

    deriveOutboundList(rmcsOutboundList, auditList, reportMap);

    auditList.forEach(
        (key, item) -> {
          ReceivableReport tempAmount =
              reportMap.get(
                  new StringBuilder()
                      .append(item.getRevenueStream())
                      .append(ReceivableAuditConstants.HYPHEN)
                      .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_AMOUNT)
                      .toString());
          ReceivableReport tempCount =
              reportMap.get(
                  new StringBuilder()
                      .append(item.getRevenueStream())
                      .append(ReceivableAuditConstants.HYPHEN)
                      .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_COUNT)
                      .toString());
          if (item.getStatus() == ReceivableStatus.INVOICED.getId()) {
            tempAmount.setSourceData(
                calculateSumAmount(item.getAmount(), tempAmount.getSourceData()));
            tempCount.setSourceData(calculateSumCount(ReceivableAuditConstants.ONE, tempCount.getSourceData()));
            if (!auditList.containsKey(item.getInvoiceId() + ReceivableStatus.SENT_TO_WD.getId())) {
              tempAmount.setRejected(
                  calculateSumAmount(item.getAmount(), tempAmount.getRejected()));
              tempCount.setRejected(calculateSumCount(ReceivableAuditConstants.ONE, tempCount.getRejected()));
            }
          }
          if (item.getStatus() == ReceivableStatus.SENT_TO_WD.getId()) {
            tempAmount.setDestinationData(
                calculateSumAmount(item.getAmount(), tempAmount.getDestinationData()));
            tempCount.setDestinationData(calculateSumCount(ReceivableAuditConstants.ONE, tempCount.getDestinationData()));
          }
          reportMap.replace(
              new StringBuilder()
                  .append(item.getRevenueStream())
                  .append(ReceivableAuditConstants.HYPHEN)
                  .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_AMOUNT)
                  .toString(),
              tempAmount);
          reportMap.replace(
              new StringBuilder()
                  .append(item.getRevenueStream())
                  .append(ReceivableAuditConstants.HYPHEN)
                  .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_COUNT)
                  .toString(),
              tempCount);
        });

    deriveReportMap(reportMap);

    List<ReceivableReport> receivableReports =
        reportMap.values().stream().collect(Collectors.toList());

    return receivableReports;
  }

  public List<ReceivableAudit> getRMCSOutboundSubList(List<ReceivableAudit> receivableAuditList) {

    List<ReceivableAudit> rmcsProcessSubList =
        receivableAuditList.stream()
            .filter(
                s -> s.getStatus() == ReceivableStatus.SENT_TO_WD.getId())
            .toList();
    Map<String, ReceivableAudit> auditList = new HashMap<>();

    deriveProcessList(rmcsProcessSubList, auditList, null);
    List<String> unprocessedListKeys = new ArrayList<>();

    auditList.forEach(
        (key, value) -> {
          if (value.getStatus() == ReceivableStatus.INVOICED.getId()
              && !auditList.containsKey(
                  value.getInvoiceId() + ReceivableStatus.SENT_TO_WD.getId())) {
            unprocessedListKeys.add(key);
          }
        });

    List<ReceivableAudit> receivableReports =
        auditList.values().stream()
            .filter(
                s ->
                    unprocessedListKeys.contains(
                        s.getInvoiceId() + ReceivableStatus.INVOICED.getId()))
            .collect(Collectors.toList());

    return receivableReports;
  }

  private void deriveOutboundList(
      List<ReceivableAudit> rmcsOutboundList,
      Map<String, ReceivableAudit> auditList,
      Map<String, ReceivableReport> reportMap) {
    rmcsOutboundList.forEach(
        item -> {
          if (auditList.containsKey(item.getInvoiceId() + item.getStatus())) {
            ReceivableAudit temp = auditList.get(item.getInvoiceId() + item.getStatus());
            if (temp.getAuditEntryCreatedDate().isBefore(item.getAuditEntryCreatedDate())) {
              auditList.replace(item.getInvoiceId() + item.getStatus(), item);
            }
          } else {
            auditList.put(item.getInvoiceId() + item.getStatus(), item);
          }
          if (!reportMap.containsKey(item.getRevenueStream())) {
            reportMap.put(
                new StringBuilder()
                    .append(item.getRevenueStream())
                    .append(ReceivableAuditConstants.HYPHEN)
                    .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_AMOUNT)
                    .toString(),
                ReceivableReport.builder()
                    .revenueStream(item.getRevenueStream())
                    .check(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_AMOUNT)
                    .sourceData(ReceivableAuditConstants.ZERO)
                    .destinationData(ReceivableAuditConstants.ZERO)
                    .rejected(ReceivableAuditConstants.ZERO)
                    .build());
            reportMap.put(
                new StringBuilder()
                    .append(item.getRevenueStream())
                    .append(ReceivableAuditConstants.HYPHEN)
                    .append(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_COUNT)
                    .toString(),
                ReceivableReport.builder()
                    .revenueStream(item.getRevenueStream())
                    .sourceData(ReceivableAuditConstants.ZERO)
                    .destinationData(ReceivableAuditConstants.ZERO)
                    .rejected(ReceivableAuditConstants.ZERO)
                    .check(ReceivableAuditConstants.RMCS_FIELD_REPORT_CHECK_COUNT)
                    .build());
          }
        });
  }
}
