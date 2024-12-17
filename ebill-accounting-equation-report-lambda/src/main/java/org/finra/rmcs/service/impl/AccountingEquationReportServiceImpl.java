package org.finra.rmcs.service.impl;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.finra.rmcs.constants.BatchEnum;
import org.finra.rmcs.constants.BatchJobStatusEnum;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.entity.AccountingEquationFFViewEntity;
import org.finra.rmcs.entity.AccountingEquationInvoiceViewEntity;
import org.finra.rmcs.entity.BatchJobEntity;
import org.finra.rmcs.entity.DataRefreshLogEntity;
import org.finra.rmcs.repo.AccountingEquationFFViewRepo;
import org.finra.rmcs.repo.AccountingEquationInvoiceViewRepo;
import org.finra.rmcs.repo.DataRefreshLogRepo;
import org.finra.rmcs.service.AccountingEquationReportService;
import org.finra.rmcs.service.BatchJobService;
import org.finra.rmcs.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service(Constants.ACCOUNTING_EQUATION_REPORT)
@Slf4j
public class AccountingEquationReportServiceImpl implements AccountingEquationReportService {
  AccountingEquationFFViewRepo accountingEquationFFViewRepo;
  AccountingEquationInvoiceViewRepo accountingEquationInvoiceViewRepo;
  private final EmailService emailService;
  private BatchJobService batchJobService;
  private DataRefreshLogRepo dataRefreshLogRepo;

  @Value("${email.accequationreport.to}")
  String toAddress;

  @Value("${email.accequationreport.from}")
  String fromAddress;

  @Value("${email.accequationreport.cc}")
  String ccAddress;

  public AccountingEquationReportServiceImpl(
      @NonNull AccountingEquationFFViewRepo accountingEquationFFViewRepo,
      @NonNull AccountingEquationInvoiceViewRepo accountingEquationInvoiceViewRepo,
      BatchJobService batchJobService,
      EmailService emailService,
      DataRefreshLogRepo dataRefreshLogRepo,
      @Value("${email.accequationreport.to}") String toAddress,
      @Value("${email.accequationreport.from}") String fromAddress,
      @Value("${email.accequationreport.cc}") String ccAddress) {
    this.accountingEquationFFViewRepo = accountingEquationFFViewRepo;
    this.accountingEquationInvoiceViewRepo = accountingEquationInvoiceViewRepo;
    this.emailService = emailService;
    this.batchJobService = batchJobService;
    this.dataRefreshLogRepo = dataRefreshLogRepo;
    this.toAddress = toAddress;
    this.fromAddress = fromAddress;
    this.ccAddress = ccAddress;
  }



  public boolean sendAccountingEquationReportAsEmail(String correlationId) {
    String methodName =
        Constants.CLASS
            + this.getClass().getSimpleName()
            + " "
            + Constants.METHOD
            + Thread.currentThread().getStackTrace()[1].getMethodName();

    BatchJobEntity savedEntity = null;
    savedEntity = batchJobService.findByName(BatchEnum.ACCOUNTING_EQUATION_REPORT_JOB.name());
    if (savedEntity != null
        && !savedEntity.getStatus().equalsIgnoreCase(BatchJobStatusEnum.FAILED.name())) {
      log.info(
          Constants.LOG_FORMAT,
          Constants.LAMBDA_NAME,
          methodName,
          correlationId,
          String.format("Account Equation Report  already sent ..Skipping further execution..."));
      return true;
    }

    log.info("Accounting Equation Report correlationId {}", correlationId);
    String reportDate = getDate();
    String emailContent = Constants.EMPTY_SPACE;
    Workbook workbook = null;
    boolean isEmailsend = false;
    String env = System.getenv(Constants.SPRING_PROFILES_ACTIVE).toUpperCase();
    String fileName = Constants.ACC_EQUATION_FILE_NAME + "_" + reportDate;
    String[] contactAddress = ccAddress.split(",");
    String subject =
        "["
            + env
            + "]"
            + "-"
            + Constants.EMPTY_SPACE
            + Constants.ACC_EQUATION_REPORT_SUBJECT
            + "_"
            + reportDate;
    List<AccountingEquationFFViewEntity> accountEquationReportData4FFList =
        accountingEquationFFViewRepo.getAccountEquationReportData4FF();

    List<AccountingEquationInvoiceViewEntity> accountEquationReportData4InvoicesList =
        accountingEquationInvoiceViewRepo.getAccountEquationReportData4Invoices();
    log.info("accountEquationReportData4FF size(): {}", accountEquationReportData4FFList.size());
    log.info(
        "accountEquationReportData4InvoicesList size(): {}",
        accountEquationReportData4InvoicesList.size());
    if (!accountEquationReportData4FFList.isEmpty()
        || !accountEquationReportData4InvoicesList.isEmpty()) {

      log.info("Accounting Equation Report generation process started");
       savedEntity =
          batchJobService.save(
              UUID.fromString(correlationId),
              BatchEnum.ACCOUNTING_EQUATION_REPORT_JOB.name(),
              BatchJobStatusEnum.STARTED.name());
      workbook = new XSSFWorkbook();
      CreationHelper createHelper = workbook.getCreationHelper();
      Font headerFont = workbook.createFont();
      headerFont.setBold(true);
      headerFont.setColor(IndexedColors.BLACK.getIndex());
      CellStyle headerCellStyle = workbook.createCellStyle();
      headerCellStyle.setFont(headerFont);
      CellStyle dateCellStyle = workbook.createCellStyle();
      dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yy"));

      if (!accountEquationReportData4FFList.isEmpty()) {
        Sheet FFAccEquationSheet =
            workbook.createSheet(Constants.FF_ACC_EQUATION_REPORT_SHEET_NAME + "_" + reportDate);
        Row FFOutBoundRow = FFAccEquationSheet.createRow(0);
        setHeaders(
            Constants.FF_ACC_EQUATION_REPORT_OUT_BOUND_HEADERS, headerCellStyle, FFOutBoundRow);
        generateAccEquationReport4FF(
            accountEquationReportData4FFList, FFAccEquationSheet, dateCellStyle, headerCellStyle);
      }

      if (!accountEquationReportData4InvoicesList.isEmpty()) {
        Sheet InvoiceAccEquationSheet =
            workbook.createSheet(
                Constants.INVOICE_ACC_EQUATION_REPORT_SHEET_NAME + "_" + reportDate);
        Row InvoiceOutBoundRow = InvoiceAccEquationSheet.createRow(0);
        setHeaders(
            Constants.INVOICE_ACC_EQUATION_REPORT_OUT_BOUND_HEADERS,
            headerCellStyle,
            InvoiceOutBoundRow);
        generateAccEquationReport4Invoice(
            accountEquationReportData4InvoicesList,
            InvoiceAccEquationSheet,
            dateCellStyle,
            headerCellStyle);
      }

      log.info("ACC_EQUATION_REPORT  generation process completed");
      byte[] reportBytes = null;
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        workbook.write(baos);
        reportBytes = baos.toByteArray();
      } catch (Exception e) {
        log.error("Exception occurred while converting XSSF to bytes {}", e.getMessage());
        savedEntity.setStatus(BatchJobStatusEnum.FAILED.name());
        batchJobService.update(savedEntity);
      }
      try {
        isEmailsend =
            emailService.sendAttachmentEMail(
                fromAddress, toAddress, ccAddress, subject, emailContent, reportBytes, fileName,true);
      } catch (Exception e) {
        log.error(
            "Exception occurred while sending Accounting Equation  report email ", e.getMessage());
        savedEntity.setStatus(BatchJobStatusEnum.FAILED.name());
        batchJobService.update(savedEntity);
      }
      if (isEmailsend) {
        log.info("Accounting Equation Report generated and sent Successfully {}", isEmailsend);
        savedEntity.setStatus(BatchJobStatusEnum.COMPLETED.name());
        batchJobService.update(savedEntity);
      } else {
        log.info("No Mail Sent for Accounting Equation Report... {}", isEmailsend);
        savedEntity.setStatus(BatchJobStatusEnum.FAILED.name());
        batchJobService.update(savedEntity);
      }
    } else {
      log.info("No Accounting Equation Report generated, No Mail Sent {}", isEmailsend);
       savedEntity =
          batchJobService.save(
              UUID.fromString(correlationId),
              BatchEnum.ACCOUNTING_EQUATION_REPORT_JOB.name(),
              BatchJobStatusEnum.NO_DATA_NO_REPORT_GENERATION.name());
    }
    return isEmailsend;
  }

  private void generateAccEquationReport4FF(
      List<AccountingEquationFFViewEntity> accountingEquationFFViewEntityList,
      Sheet sheet,
      CellStyle dateCellStyle,
      CellStyle headerCellStyle) {
    int rowId = 1;
    DecimalFormat df = new DecimalFormat("0.00");
    for (AccountingEquationFFViewEntity entity : accountingEquationFFViewEntityList) {
      Row row = sheet.createRow(rowId++);
      row.createCell(0).setCellValue(entity.getRevenueStream());
      row.createCell(1).setCellValue(entity.getCustomerId());
      row.createCell(2).setCellValue(df.format(entity.getBeginningBalance()));
      row.createCell(3).setCellValue(df.format(entity.getEndingBalance()));
      row.createCell(4).setCellValue(df.format(entity.getCalculatedBalance()));
      row.createCell(5).setCellValue(df.format(entity.getVariance()));
    }
  }

  private void generateAccEquationReport4Invoice(
      List<AccountingEquationInvoiceViewEntity> accountingEquationInvoiceViewEntityList,
      Sheet sheet,
      CellStyle dateCellStyle,
      CellStyle headerCellStyle) {
    int rowId = 1;
    DecimalFormat df = new DecimalFormat("0.00");
    for (AccountingEquationInvoiceViewEntity entity : accountingEquationInvoiceViewEntityList) {
      Row row = sheet.createRow(rowId++);
      Cell dateCell = row.createCell(4);
      dateCell.setCellValue(entity.getInvoiceDate());
      dateCell.setCellStyle(dateCellStyle);
      row.createCell(0).setCellValue(entity.getRevenueStream());
      row.createCell(1).setCellValue(entity.getOrgId());
      row.createCell(2).setCellValue(entity.getCustomerId());
      row.createCell(3).setCellValue(entity.getInvoiceId());
      row.createCell(5).setCellValue(df.format(entity.getInvoiceAmount()));
      row.createCell(6).setCellValue(df.format(entity.getInvoiceBalance()));
      row.createCell(7).setCellValue(df.format(entity.getCalculatedInvoiceBalance()));
      row.createCell(8).setCellValue(df.format(entity.getVariance()));
    }
  }

  private void setHeaders(String[] headers, CellStyle headerCellStyle, Row headerRow) {
    for (int col = 0; col < headers.length; col++) {
      Cell cell = headerRow.createCell(col);
      cell.setCellValue(headers[col]);
      cell.setCellStyle(headerCellStyle);
    }
  }

  private static String getDate() {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMddyyyy");
    return ZonedDateTime.now()
        .withZoneSameInstant(ZoneId.of(Constants.EST_TIME_ZONE))
        .toLocalDateTime()
        .format(dateTimeFormatter);
  }
}
