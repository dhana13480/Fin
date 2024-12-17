package org.finra.rmcs.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.finra.rmcs.entity.AccountingEquationFFViewEntity;
import org.finra.rmcs.entity.AccountingEquationInvoiceViewEntity;
import org.finra.rmcs.entity.BatchJobEntity;
import org.finra.rmcs.entity.DataRefreshLogEntity;
import org.finra.rmcs.repo.AccountingEquationFFViewRepo;
import org.finra.rmcs.repo.AccountingEquationInvoiceViewRepo;
import org.finra.rmcs.repo.BatchJobRepo;
import org.finra.rmcs.repo.DataRefreshLogRepo;
import org.finra.rmcs.service.EmailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

@SpringJUnitConfig
public class AccountingEquationReportServiceImplTest {
  @Mock AccountingEquationReportServiceImpl spyService;
  @InjectMocks AccountingEquationReportServiceImpl accountingEquationReportService;
  @Mock AccountingEquationFFViewRepo accountingEquationFFViewRepo;
  @Mock AccountingEquationInvoiceViewRepo accountingEquationInvoiceViewRepo;
  @Mock BatchJobRepo batchJobRepo;
  @Mock EmailService emailService;
  @Mock BatchJobServiceImpl batchJobService;
  @Mock
  DataRefreshLogRepo dataRefreshLogRepo;

  AccountingEquationFFViewEntity ffViewEntity;
  AccountingEquationInvoiceViewEntity invoiceViewEntity;
  BatchJobEntity batchJobEntity;
  String testEmailAddress = "test@finra.org";
  String result="FAILED";
  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(accountingEquationReportService, "toAddress", "test@finra.org");
    ReflectionTestUtils.setField(accountingEquationReportService, "fromAddress", "test@finra.org");
    ReflectionTestUtils.setField(accountingEquationReportService, "ccAddress", "test@finra.org");
    ffViewEntity =
        AccountingEquationFFViewEntity.builder()
            .revenueStream("CRDRG")
            .beginningBalance(new BigDecimal(10))
            .endingBalance(new BigDecimal("20"))
            .calculatedBalance(new BigDecimal(30))
            .variance(new BigDecimal(50))
            .build();
    invoiceViewEntity =
        AccountingEquationInvoiceViewEntity.builder()
            .revenueStream("CRDRG")
            .customerId("13109-CRR")
            .invoiceAmount(new BigDecimal(10))
            .calculatedInvoiceBalance(new BigDecimal(30))
            .variance(new BigDecimal(50))
            .invoiceBalance(new BigDecimal(40))
            .build();

    batchJobEntity = BatchJobEntity.builder().id(UUID.randomUUID()).status("Status").build();
  }

  @Test
  public void testSendAccoutingEquationReportAsEmail() throws Exception {
    String subject = "email subject";
    String emailContent = "email emailContent";
    when(accountingEquationFFViewRepo.getAccountEquationReportData4FF())
        .thenReturn(List.of(ffViewEntity));
    when(accountingEquationInvoiceViewRepo.getAccountEquationReportData4Invoices())
        .thenReturn(List.of(invoiceViewEntity));
    when(batchJobService.save(any(), anyString(), anyString())).thenReturn(batchJobEntity);
    doReturn(true)
        .when(emailService)
        .sendAttachmentEMail(
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            any(byte[].class),
            anyString(),any(boolean.class));
    boolean isMaildSend=accountingEquationReportService.sendAccountingEquationReportAsEmail(
        UUID.randomUUID().toString());
    Assertions.assertEquals(true,isMaildSend);
  }


  @Test
  public void testNot_SendAccoutingEquationReportAsEmail() throws Exception {
    String subject = "email subject";
    String emailContent = "email emailContent";
    when(accountingEquationFFViewRepo.getAccountEquationReportData4FF())
        .thenReturn(Collections.EMPTY_LIST);
    when(accountingEquationInvoiceViewRepo.getAccountEquationReportData4Invoices())
        .thenReturn(Collections.EMPTY_LIST);
    when(batchJobService.save(any(), anyString(), anyString())).thenReturn(batchJobEntity);
    doReturn(true)
        .when(emailService)
        .sendAttachmentEMail(
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            any(byte[].class),
            anyString(),any(boolean.class));
    boolean isMaildSend=accountingEquationReportService.sendAccountingEquationReportAsEmail(
        UUID.randomUUID().toString());
    Assertions.assertEquals(false,isMaildSend);
  }


  @Test
  public void testSendAccoutingEquationReportAsEmailException() throws Exception {
    String subject = "email subject";
    String emailContent = "email emailContent";
    when(accountingEquationFFViewRepo.getAccountEquationReportData4FF())
        .thenReturn(List.of(ffViewEntity));
    when(accountingEquationInvoiceViewRepo.getAccountEquationReportData4Invoices())
        .thenReturn(List.of(invoiceViewEntity));
    when(batchJobService.save(any(), anyString(), anyString())).thenReturn(batchJobEntity);
    doThrow(RuntimeException.class)
        .when(emailService)
        .sendAttachmentEMail(
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            any(byte[].class),
            anyString(),any(boolean.class));
   boolean isMaildSend= accountingEquationReportService.sendAccountingEquationReportAsEmail(
        UUID.randomUUID().toString());
    Assertions.assertEquals(false,isMaildSend);
  }


}
