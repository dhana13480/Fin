package org.finra.rmcs.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.finra.rmcs.FileReaderUtilTest;
import org.finra.rmcs.dto.Receivable;
import org.finra.rmcs.dto.ReceivableItem;
import org.finra.rmcs.entity.ReceivableEntity;
import org.finra.rmcs.entity.ReceivableItemEntity;
import org.finra.rmcs.entity.RevenueStreamEntity;
import org.finra.rmcs.repo.ReceivableRepo;
import org.finra.rmcs.repo.ReceivableRevenueStreamRepo;
import org.finra.rmcs.utils.ReceivableUtil;
import org.finra.rmcs.utils.S3Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;

@SpringJUnitConfig
public class InvoiceWdServiceImplTest {

  private static ReceivableEntity receivableEntity;
  private static RevenueStreamEntity revenueStreamEntity;
  private static Receivable receivable;
  private static List<String> jsonLines;
  @Mock
  ReceivableRepo receivableRepo;
  @Mock
  ReceivableRevenueStreamRepo revenueStreamRepo;
  @Mock
  S3Client s3Client;

  @InjectMocks
  InvoiceWdServiceImpl invoiceWdService;
  @Mock
  private ReceivableUtil receivableUtil;

  @BeforeAll
  public static void initReceivableUtil() throws IOException {
    MockedStatic<S3Util> mockedStatic = mockStatic(S3Util.class);
    receivableEntity = new ReceivableEntity();
    receivableEntity.setPaymentReceived("Y");
    receivableEntity.setReceivableItems(
        Arrays.asList(new ReceivableItemEntity(), new ReceivableItemEntity()));
    receivable = new Receivable();
    receivable.setLines(Arrays.asList(new ReceivableItem()));
    Gson gson = new Gson();
    jsonLines =
        gson.fromJson(FileReaderUtilTest.getResourceContent("ReceivableJson.json"), List.class);
    revenueStreamEntity =
        gson.fromJson(
            FileReaderUtilTest.getResourceContent("RevenueStreamEntity.json"),
            RevenueStreamEntity.class);
  }


  @BeforeEach
  public void init() {
    invoiceWdService =
        new InvoiceWdServiceImpl(
            "",
            "",
            "",
            "",
            "",
            "",
            receivableRepo,
            revenueStreamRepo,
            receivableUtil,
            "1",
            "1");
  }

  @Test
  public void emptyListsentReceivableInvoiceToWdTest() {
    when(revenueStreamRepo.findAll()).thenReturn(Arrays.asList(revenueStreamEntity));
    invoiceWdService.sentReceivableInvoiceToWd("", null);
    verify(receivableRepo, never()).updateReceivablesFromInvoicedToSendToWD(any());
  }

  @Test
  public void listSentReceivableInvoiceToWdExternalTest() {
    when(receivableRepo.getInvoicedReceivable(any())).thenReturn(Arrays.asList(receivableEntity));
    when(revenueStreamRepo.findAll()).thenReturn(Arrays.asList(revenueStreamEntity));
    when(S3Util.getExternalS3Client(any())).thenReturn(s3Client);
    when(s3Client.putObject((PutObjectRequest) any(), (RequestBody) any())).thenReturn(null);
    when(receivableUtil.convertReceivableItemEntityToDto(any(), any()))
        .thenReturn(receivable.getLines().get(0));
    when(receivableUtil.convertReceivableEntityToDto(any())).thenReturn(receivable);
    when(receivableUtil.getJsonLines(any())).thenReturn(jsonLines);
    invoiceWdService.sentReceivableInvoiceToWd(null, null);
    verify(receivableRepo, times(0)).getInvoicedReceivable(any());
  }

  @Test
  public void listSentReceivableInvoiceToWdInternalTest() {
    ReflectionTestUtils.setField(invoiceWdService, "writeToExternalBucket", false);
    ReflectionTestUtils.setField(invoiceWdService, "maxLineCount", "3");
    when(receivableRepo.getInvoicedReceivable(any())).thenReturn(Arrays.asList());
    when(revenueStreamRepo.findAll()).thenReturn(Arrays.asList(revenueStreamEntity));
    when(S3Util.getInternalS3Client()).thenReturn(s3Client);
    when(s3Client.putObject((PutObjectRequest) any(), (RequestBody) any())).thenReturn(null);
    when(receivableUtil.convertReceivableItemEntityToDto(any(), any()))
        .thenReturn(receivable.getLines().get(0));
    when(receivableUtil.convertReceivableEntityToDto(any())).thenReturn(receivable);
    when(receivableUtil.getJsonLines(any())).thenReturn(jsonLines);
    invoiceWdService.sentReceivableInvoiceToWd(null, null);
    verify(receivableRepo, times(0)).getInvoicedReceivable(any());
  }

  @Test
  public void sentReceivableInvoiceToWdExceptionTest() {
    mockStatic(ReceivableUtil.class);
    ReflectionTestUtils.setField(invoiceWdService, "writeToExternalBucket", false);
    ReflectionTestUtils.setField(invoiceWdService, "maxLineCount", "3");
    when(revenueStreamRepo.findAll()).thenReturn(Arrays.asList(revenueStreamEntity));
    when(S3Util.getInternalS3Client()).thenReturn(s3Client);
    Assertions.assertThrows(NullPointerException.class, () -> {
      invoiceWdService.sentReceivableInvoiceToWd("", "");
    });
  }

  @Test
  public void testSentReceivableInvoiceToWd_sendToWDFlagFalse() {
    RevenueStreamEntity revenueStreamEntity = new RevenueStreamEntity();
    revenueStreamEntity.setRevenueStreamName("EMSBU");
    revenueStreamEntity.setSendToWD(false);
    when(revenueStreamRepo.findAll()).thenReturn(Arrays.asList(revenueStreamEntity));
    invoiceWdService.sentReceivableInvoiceToWd("", "EMSBU");
    verify(receivableRepo, times(0)).getInvoicedReceivable(any());
  }
}
