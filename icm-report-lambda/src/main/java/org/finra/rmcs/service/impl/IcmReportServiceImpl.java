package org.finra.rmcs.service.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.finra.rmcs.common.service.email.EmailService;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.constants.ReceivableAuditConstants;
import org.finra.rmcs.dto.PaymentReport;
import org.finra.rmcs.dto.ReceivableAudit;
import org.finra.rmcs.dto.ReceivableReport;
import org.finra.rmcs.entity.PaymentTrackingEntity;
import org.finra.rmcs.repo.ReceivableRevenueStreamRepo;
import org.finra.rmcs.service.IcmReportService;
import org.finra.rmcs.service.PaymentAuditService;
import org.finra.rmcs.service.PaymentTrackingService;
import org.finra.rmcs.service.ReceivableAuditService;
import org.finra.rmcs.util.ReceivableAuditUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IcmReportServiceImpl implements IcmReportService {

  private final ReceivableAuditService receivableAuditService;
  private final PaymentAuditService paymentAuditService;
  private final ReceivableAuditUtil receivableAuditUtil;
  private final EmailService emailService;
  private final ReceivableRevenueStreamRepo receivableRevenueStreamRepo;
  private final PaymentTrackingService paymentTrackingService;

  private String toAddress;

  private String fromAddress;

  private String ccAddress;
  @Autowired
  public IcmReportServiceImpl(
      @Value("${email.icm.to}") String toAddress,
      @Value("${email.icm.from}") String fromAddress,
      @Value("${email.icm.cc}") String ccAddress,
      ReceivableAuditService receivableAuditService,
      PaymentAuditService paymentAuditService,
      ReceivableAuditUtil receivableAuditUtil,
      EmailService emailService,
      ReceivableRevenueStreamRepo receivableRevenueStreamRepo,
      PaymentTrackingService paymentTrackingService) {
    this.toAddress = toAddress;
    this.fromAddress = fromAddress;
    this.ccAddress = ccAddress;
    this.receivableAuditService = receivableAuditService;
    this.paymentAuditService = paymentAuditService;
    this.receivableAuditUtil = receivableAuditUtil;
    this.emailService = emailService;
    this.receivableRevenueStreamRepo = receivableRevenueStreamRepo;
    this.paymentTrackingService = paymentTrackingService;
  }

  @Override
  public boolean sendICMReportAsEmail(ZonedDateTime icmReportDate) {
    StringBuilder emailBodyBuilder = null;
    String emailContent = null;
    Workbook workbook = null;
    boolean isEmailsend = false;
    LocalDate previousEntryDate = null;
    String env = System.getenv(Constants.SPRING_PROFILES_ACTIVE).toUpperCase();
    String fileName =
        ReceivableAuditConstants.ICM_FILE_NAME + "-" + icmReportDate.toLocalDate() + ".xlsx";
    String[] contactAddress = ccAddress.split(",");
    String subject =
        "["
            + env
            + "]"
            + "-"
            + ReceivableAuditConstants.ICM_REPORT_SUBJECT
            + "-"
            + icmReportDate.toLocalDate();
    List<ReceivableAudit> receivableAuditList =
        receivableAuditService.findByAuditEntryCreatedDate(icmReportDate);
    List<PaymentReport> paymentReportList = paymentAuditService.findByCreatedDate(icmReportDate);
    List<PaymentTrackingEntity> paymentTrackingEntityList = paymentTrackingService.findByCreatedDate(icmReportDate);
    log.info("AR Transactions size: {}", paymentTrackingEntityList.size());
    if (receivableAuditList.isEmpty() && paymentReportList.isEmpty() && paymentTrackingEntityList.isEmpty()) {
      log.info("No data found for previous date. sending email with out report");
      emailBodyBuilder = new StringBuilder();
      emailBodyBuilder
          .append(ReceivableAuditConstants.EMAIL_BODY_NO_DATA_TXT)
          .append("\n\n")
          .append("\n")
          .append("Thanks")
          .append("\n")
          .append("RMCS Team");
      emailContent =
          emailBodyBuilder
              .toString()
              .replace("{date}", icmReportDate.toLocalDate().toString())
              .replace(
                  "{contactAddress}", contactAddress[0] != null ? contactAddress[0] : ccAddress);
      try {
        isEmailsend =
            emailService.sendAttachmentEMail(
                fromAddress, toAddress, ccAddress, subject, emailContent, null, null);
      } catch (MessagingException e) {
        log.error("Exception occurred while sending icm report email " + e.getMessage());
      }
      return isEmailsend;
    } else {
      log.info("ICM report generation process started");
      workbook = new XSSFWorkbook();
      CreationHelper createHelper = workbook.getCreationHelper();
      // Create a Font for styling header cells
      Font headerFont = workbook.createFont();
      headerFont.setBold(true);
      headerFont.setFontHeightInPoints((short) 16);
      headerFont.setColor(IndexedColors.BLACK.getIndex());
      // Create a CellStyle with the font
      CellStyle headerCellStyle = workbook.createCellStyle();
      headerCellStyle.setFont(headerFont);
      Sheet icmOutBoundSheet = workbook.createSheet(ReceivableAuditConstants.RMCS_OUT_BOUND);
      CellStyle dateCellStyle = workbook.createCellStyle();
      dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yy"));
      // Start RMCS OUT BOUND sheet creation
      Row rmcsOutBoundRow = icmOutBoundSheet.createRow(0);
      setHeaders(ReceivableAuditConstants.ICM_OUT_BOUND_HEADERS, headerCellStyle, rmcsOutBoundRow);
      List<ReceivableReport> rmcsOutBoundList =
          receivableAuditUtil.getRMCSOutboundList(receivableAuditList);

      if (!rmcsOutBoundList.isEmpty() || !paymentReportList.isEmpty() || !paymentTrackingEntityList.isEmpty()) {
        List<ReceivableAudit> rmcsOutboundSubList =
            receivableAuditUtil.getRMCSOutboundSubList(receivableAuditList);
        generateReport(
            rmcsOutBoundList,
            paymentReportList,
            rmcsOutboundSubList,
            paymentTrackingEntityList,
            ReceivableAuditConstants.ICM_RECEIVABLE_OUTBOUND_DATA,
            ReceivableAuditConstants.ICM_PAYMENT_OUTBOUND_DATA,
            "outbound",
            icmReportDate.toLocalDate(),
            icmOutBoundSheet,
            dateCellStyle,
            headerCellStyle);
      }
      // End ICM OUT BOUND sheet creation

      //  End ICM Excel Sheet
      log.info("ICM report generation process completed");
      byte[] icmReportBytes = null;
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        workbook.write(baos);
        icmReportBytes = baos.toByteArray();
      } catch (Exception e) {
        log.error("Exception occurred while converting XSSF to bytes {}", e.getMessage());
      }
      emailBodyBuilder = new StringBuilder();
      emailBodyBuilder
          .append(ReceivableAuditConstants.EMAIL_BODY_DATA_TXT)
          .append("\n\n")
          .append("\n")
          .append("Thanks")
          .append("\n")
          .append("RMCS Team");

      emailContent =
          emailBodyBuilder
              .toString()
              .replace("{date}", icmReportDate.toLocalDate().toString())
              .replace(
                  "{contactAddress}", contactAddress[0] != null ? contactAddress[0] : ccAddress);
      try {
        isEmailsend =
            emailService.sendAttachmentEMail(
                fromAddress, toAddress, ccAddress, subject, emailContent, icmReportBytes, fileName);
      } catch (MessagingException e) {
        log.error("Exception occurred while sending icm report email ", e.getMessage());
      }
      if (isEmailsend) log.info("ICM Report generated and sent Successfully {}", isEmailsend);
    }
    return isEmailsend;
  }

  private void generateReport(
      List<ReceivableReport> receivableReportList,
      List<PaymentReport> paymentReportList,
      List<ReceivableAudit> rejectionList,
      List<PaymentTrackingEntity> paymentTrackingEntityList,
      String[] icmReceivableReportData,
      String[] icmPaymentReportData,
      String reportTabName,
      LocalDate icmReportDate,
      Sheet sheet,
      CellStyle dateCellStyle,
      CellStyle headerCellStyle) {
    int rowId = 1;

    List<String> revenueStreams =
        receivableRevenueStreamRepo.findActiveRevenueStream().stream().sorted().toList();
    Map<String, List<ReceivableReport>> receivableReportMap =
        receivableReportList.stream()
            .collect(
                Collectors.groupingBy(ReceivableReport::getRevenueStream, Collectors.toList()));
    Map<String, List<PaymentReport>> paymentReportMap =
        paymentReportList.stream()
            .collect(Collectors.groupingBy(PaymentReport::getRevenueStream, Collectors.toList()));

    for (String revenueStream : revenueStreams) {
      List<ReceivableReport> receivableReports =
          receivableReportMap.getOrDefault(revenueStream, Collections.emptyList());
      List<PaymentReport> paymentReports =
          paymentReportMap.getOrDefault(revenueStream, Collections.emptyList());

      if (ReceivableAuditConstants.IGNORE_REVENUE_STREAM_LIST.contains(revenueStream)
          || (receivableReports.isEmpty() && paymentReports.isEmpty())) {
        continue;
      }
      for (ReceivableReport receivableReport : receivableReports) {
        Row row = sheet.createRow(rowId++);
        Cell dateCell = row.createCell(0);
        dateCell.setCellValue(icmReportDate);
        dateCell.setCellStyle(dateCellStyle);
        row.createCell(1).setCellValue(icmReceivableReportData[0]);
        row.createCell(2).setCellValue(icmReceivableReportData[1]);
        row.createCell(3).setCellValue(revenueStream);
        row.createCell(4).setCellValue(icmReceivableReportData[2]);
        row.createCell(5).setCellValue(receivableReport.getCheck());
        row.createCell(6).setCellValue(receivableReport.getDestinationData());
        row.createCell(7).setCellValue(StringUtils.EMPTY);
        row.createCell(8).setCellValue(StringUtils.EMPTY);
        row.createCell(9).setCellValue(StringUtils.EMPTY);
      }

      for (PaymentReport paymentReport : paymentReports) {
        Row row = sheet.createRow(rowId++);
        Cell dateCell = row.createCell(0);
        dateCell.setCellValue(icmReportDate);
        dateCell.setCellStyle(dateCellStyle);
        row.createCell(1).setCellValue(icmPaymentReportData[0]);
        row.createCell(2).setCellValue(icmPaymentReportData[1]);
        row.createCell(3).setCellValue(revenueStream);
        row.createCell(4).setCellValue(icmPaymentReportData[2]);
        row.createCell(5).setCellValue(paymentReport.getCheck());
        row.createCell(6).setCellValue(paymentReport.getSourceData());
        row.createCell(7).setCellValue(StringUtils.EMPTY);
        row.createCell(8).setCellValue(StringUtils.EMPTY);
        row.createCell(9).setCellValue(StringUtils.EMPTY);
      }
      sheet.createRow(rowId++);

    }

    //for Ebill Transactions
    Map<String,BigDecimal> totalAmountForEachRevenueStream = paymentTrackingEntityList
            .stream().sorted(Comparator.comparing(PaymentTrackingEntity::getBusinessUnit))
            .collect(Collectors.groupingBy(PaymentTrackingEntity::getBusinessUnit, LinkedHashMap::new,
            Collectors.reducing(BigDecimal.ZERO,PaymentTrackingEntity::getPaymentAmount,BigDecimal::add)));

    Map<String, Long> revenueStreamCount = paymentTrackingEntityList
            .stream().collect(Collectors.groupingBy(PaymentTrackingEntity::getBusinessUnit,
            Collectors.counting()));

    if (!totalAmountForEachRevenueStream.isEmpty()) {
      for(String  businessUnit :totalAmountForEachRevenueStream.keySet()) {
         for (int i = 0; i <= 1; i++) {
          Row row = sheet.createRow(rowId++);
          Cell dateCell = row.createCell(0);
          dateCell.setCellValue(icmReportDate);
          dateCell.setCellStyle(dateCellStyle);
          row.createCell(1).setCellValue(ReceivableAuditConstants.ICM_AFT_SOURCE);
          row.createCell(2).setCellValue(ReceivableAuditConstants.ICM_AFT_DESTINATION);
          row.createCell(3).setCellValue(businessUnit);
          row.createCell(4).setCellValue(ReceivableAuditConstants.ICM_AFT_TRANSACTION_TYPE);
          if (i == 0) {
            row.createCell(5).setCellValue(ReceivableAuditConstants.ICM_AFT_CHECK);
            row.createCell(6).setCellValue(String.valueOf(totalAmountForEachRevenueStream.get(businessUnit)));
            row.createCell(7).setCellValue(StringUtils.EMPTY);
          }
          if (i == 1) {
            row.createCell(5).setCellValue(ReceivableAuditConstants.ICM_AFT_COUNT);
            row.createCell(6).setCellValue(String.valueOf(revenueStreamCount.get(businessUnit)));
            row.createCell(7).setCellValue(StringUtils.EMPTY);
          }
          row.createCell(8).setCellValue(StringUtils.EMPTY);
          row.createCell(9).setCellValue(StringUtils.EMPTY);
        }
        sheet.createRow(rowId++);
      }
    }

  }

  private void setHeaders(String[] headers, CellStyle headerCellStyle, Row headerRow) {
    for (int col = 0; col < headers.length; col++) {
      Cell cell = headerRow.createCell(col);
      cell.setCellValue(headers[col]);
      cell.setCellStyle(headerCellStyle);
    }
  }
}
