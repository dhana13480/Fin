package org.finra.rmcs.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.finra.rmcs.FileReaderUtilTest;
import org.finra.rmcs.entity.ReceivableEntity;
import org.finra.rmcs.repo.ReceivableRepo;
import org.finra.rmcs.service.impl.InvoiceServiceImpl;
import org.finra.rmcs.utils.Utils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class InvoiceServiceTest {

  private static ReceivableEntity receivableEntity;
  @Mock
  private ReceivableRepo receivableRepo;

  @Mock
  private ReceivableRevenueStreamService receivableRevenueStreamService;
  @Mock
  private SalesItemService salesItemService;

  @Mock
  private Utils utils;

  @InjectMocks
  private InvoiceServiceImpl invoiceService;

  @BeforeAll
  public static void initReceivables() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    receivableEntity =
        objectMapper.readValue(
            FileReaderUtilTest.getResourceContent("ReceivablesRequest.json"),
            ReceivableEntity.class);
  }

  @Test
  public void convertReceivableToInvoiceTestSuccess() {
    when(receivableRepo.getReadyToBillReceivableOrderByCreatedDateDesc(anyList()))
        .thenReturn(Collections.singletonList(receivableEntity));
    ReceivableEntity receivable = new ReceivableEntity();
    receivable.setInvoiceId("testId");
    receivable.setRevenueStream("APIBI");
    when(utils.updateReceivable(any(), anyList())).thenReturn(receivable);
    when(receivableRepo.saveAll(any())).thenReturn(Collections.singletonList(receivableEntity));
    invoiceService.convertReceivableToInvoice("");
    verify(receivableRepo).saveAll(any());
  }

  @Test
  public void convertReceivableToInvoiceTestReceivableListEmpty() {
    when(receivableRepo.getReadyToBillReceivableOrderByCreatedDateDesc(anyList()))
        .thenReturn(List.of());
    invoiceService.convertReceivableToInvoice("");
    verify(receivableRepo, Mockito.never()).saveAll(any());
  }

  @Test
  public void convertReceivableToInvoiceTestReceivableListNull() {
    when(receivableRepo.getReadyToBillReceivableOrderByCreatedDateDesc(anyList())).thenReturn(null);
    invoiceService.convertReceivableToInvoice("");
    verify(receivableRepo, Mockito.never()).saveAll(any());
  }
}
