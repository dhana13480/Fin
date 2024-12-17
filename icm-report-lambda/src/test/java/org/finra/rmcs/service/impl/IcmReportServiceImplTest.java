package org.finra.rmcs.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.mail.MessagingException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.finra.rmcs.common.service.email.EmailService;
import org.finra.rmcs.constants.ReceivableAuditConstants;
import org.finra.rmcs.dto.PaymentReport;
import org.finra.rmcs.dto.ReceivableAudit;
import org.finra.rmcs.dto.ReceivableReport;
import org.finra.rmcs.repo.ReceivableRevenueStreamRepo;
import org.finra.rmcs.service.PaymentAuditService;
import org.finra.rmcs.service.PaymentTrackingService;
import org.finra.rmcs.service.ReceivableAuditService;
import org.finra.rmcs.util.ReceivableAuditUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
public class IcmReportServiceImplTest {

  @Mock
  ReceivableAuditService receivableAuditService;
  @Mock
  PaymentAuditService paymentAuditService;
  @Mock
  ReceivableAuditUtil receivableAuditUtil;
  @Mock
  EmailService emailService;
  @Mock
  ReceivableRevenueStreamRepo receivableRevenueStreamRepo;
  @Mock
  PaymentTrackingService paymentTrackingService;
  IcmReportServiceImpl icmReportServiceImpl;

  String testEmailAddress = "test@finra.org";

  @BeforeEach
  public void init() {
    icmReportServiceImpl = new IcmReportServiceImpl(testEmailAddress, testEmailAddress,
        testEmailAddress, receivableAuditService, paymentAuditService, receivableAuditUtil,
        emailService, receivableRevenueStreamRepo, paymentTrackingService);
  }

  @Test
  public void sendICMReportAsEmailEmptyTest() throws MessagingException {
    List<ReceivableAudit> list = new ArrayList<>();
    LocalDate date = LocalDate.now().minusDays(1);
    ZoneId timeZone = ZoneId.of("America/New_York");
    doReturn(list).when(receivableAuditService).findByAuditEntryCreatedDate(
        ZonedDateTime.of(date.atStartOfDay(), timeZone));
    String subject =
        "[DEV]-" + ReceivableAuditConstants.ICM_REPORT_SUBJECT + "-" + date;
    StringBuilder emailBodyBuilder = new StringBuilder();
    emailBodyBuilder
        .append(ReceivableAuditConstants.EMAIL_BODY_NO_DATA_TXT)
        .append("\n\n")
        .append("\n")
        .append("Thanks")
        .append("\n")
        .append("RMCS Team");
    String emailContent =
        emailBodyBuilder
            .toString()
            .replace("{date}", date.toString())
            .replace(
                "{contactAddress}", testEmailAddress);
    Assertions.assertEquals(false,
        icmReportServiceImpl.sendICMReportAsEmail(ZonedDateTime.of(date.atStartOfDay(), timeZone)));
    verify(emailService, times(1)).sendAttachmentEMail(testEmailAddress, testEmailAddress,
        testEmailAddress, subject, emailContent, null, null);
  }

  @Test
  public void sendICMReportAsEmailSuccessTest()
      throws MessagingException {
    ReceivableAudit receivableAudit = ReceivableAudit.builder()
        .id(UUID.fromString("07e41aee-7281-434d-a8b7-7b8206237fbb"))
        .action("INSERT")
        .status(12)
        .amount("200")
        .revenueStream("APIBI")
        .invoiceId("API12345")
        .build();
    PaymentReport paymentReport = PaymentReport.builder().revenueStream("MQPBI").check("Count")
        .sourceData("12").build();
    LocalDate date = LocalDate.of(2023, 06, 30);
    ZoneId timeZone = ZoneId.of("America/New_York");
    doReturn(List.of(receivableAudit)).when(receivableAuditService)
        .findByAuditEntryCreatedDate(ZonedDateTime.of(date.atStartOfDay(), timeZone));
    doReturn(List.of(paymentReport)).when(paymentAuditService)
        .findByCreatedDate(ZonedDateTime.of(date.atStartOfDay(), timeZone));
    ReceivableReport receivableReport = ReceivableReport.builder().revenueStream("APIBI").build();
    doReturn(List.of(receivableReport)).when(receivableAuditUtil).getRMCSOutboundList(any());
    doReturn(true).when(emailService)
        .sendAttachmentEMail(anyString(), anyString(), anyString(), anyString(), anyString(),
            any(byte[].class), anyString());
    Assertions.assertEquals(true,
        icmReportServiceImpl.sendICMReportAsEmail(ZonedDateTime.of(date.atStartOfDay(), timeZone)));
  }

  @Test
  public void sendICMReportAsEmailExceptionTest()
      throws MessagingException {
    ReceivableAudit receivableAudit = ReceivableAudit.builder()
        .id(UUID.fromString("07e41aee-7281-434d-a8b7-7b8206237fbb"))
        .action("INSERT")
        .status(12)
        .amount("200")
        .revenueStream("APIBI")
        .invoiceId("API12345")
        .build();
    List<ReceivableAudit> list = new ArrayList<>();
    list.add(receivableAudit);
    LocalDate date = LocalDate.of(2023, 06, 30);
    ZoneId timeZone = ZoneId.of("America/New_York");
    doReturn(list).when(receivableAuditService)
        .findByAuditEntryCreatedDate(ZonedDateTime.of(date.atStartOfDay(), timeZone));
    doThrow(MessagingException.class).when(emailService)
        .sendAttachmentEMail(anyString(), anyString(), anyString(), anyString(), anyString(),
            any(byte[].class), anyString());
    Assertions.assertNotNull(list);
    Assertions.assertEquals(false,
        icmReportServiceImpl.sendICMReportAsEmail(ZonedDateTime.of(date.atStartOfDay(), timeZone)));
  }
}