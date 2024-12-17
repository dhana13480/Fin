package org.finra.rmcs.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.google.gson.Gson;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.finra.rmcs.FileReaderUtilTest;
import org.finra.rmcs.dto.Receivable;
import org.finra.rmcs.dto.ReceivableItem;
import org.finra.rmcs.entity.ReceivableEntity;
import org.finra.rmcs.entity.ReceivableItemEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class ReceivableUtilTest {

  private static ReceivableEntity receivableEntity;

  private static ReceivableEntity receivableEntityWithAmountNull;
  private static Receivable receivable;
  private static List<String> jsonLines;
  @InjectMocks
  private ReceivableUtil receivableUtil;

  @BeforeAll
  public static void initReceivableUtil() throws IOException {
    Gson gson = new Gson();
    receivableEntity = new ReceivableEntity();
    receivableEntity.setId(UUID.fromString("e95f92a8-2ace-4ca2-82e9-03d2fbd19828"));
    receivableEntity.setRevenueStreamReceivableId("API55415");
    receivableEntity.setCustomerId("API55415");
    receivableEntity.setRevenueStream("APIBI");
    receivableEntity.setInvoiceId("API55415");
    receivableEntity.setTotalLine(2);
    receivableEntity.setAmount(new BigDecimal("20.000000"));
    receivableEntity.setInvoiceDate(ZonedDateTime.parse("2023-08-02T01:00:57.698Z[UTC]"));

    ReceivableItemEntity receivableItemEntity1 = new ReceivableItemEntity();
    receivableItemEntity1.setCrdTransactionDate(
        ZonedDateTime.parse("2023-08-02T01:00:57.698Z[UTC]"));
    receivableItemEntity1.setFromDate(ZonedDateTime.parse("2023-08-02T01:00:57.698Z[UTC]"));
    receivableItemEntity1.setToDate(ZonedDateTime.parse("2023-08-02T01:00:57.698Z[UTC]"));
    receivableItemEntity1.setSourceTransDate(ZonedDateTime.parse("2023-08-02T01:00:57.698Z[UTC]"));
    receivableItemEntity1.setCaseOpenDate(ZonedDateTime.parse("2023-08-02T01:00:57.698Z[UTC]"));

    receivableEntity.setReceivableItems(Arrays.asList(receivableItemEntity1));
    receivableEntityWithAmountNull = new ReceivableEntity();
    receivableEntityWithAmountNull.setAmount(null);

    receivable = new Receivable();
    receivable.setId(UUID.fromString("e95f92a8-2ace-4ca2-82e9-03d2fbd19828"));
    receivable.setRevenueStreamReceivableId("API55415");
    receivable.setCompany("FINRA");
    receivable.setCustomerId("5956307679-API");
    receivable.setInvoiceDate("2023-02-03T00:00:00");
    receivable.setCreatedDate(LocalDateTime.parse("2023-02-01T22:34:29.439917"));
    receivable.setRevenueStream("APIBI");
    receivable.setInvoiceId("API55415");
    receivable.setTotalLine(2);
    receivable.setAmount(new BigDecimal("20.00"));
    receivable.setClearingNumber("clearingNumber");
    receivable.setMpID("mpID");
    receivable.setInvoiceStatus("Draft");
    receivable.setLegacy(false);
    ReceivableItem receivableItem1 = new ReceivableItem();
    receivableItem1.setId(UUID.fromString("e95f92a8-2ace-4ca2-82e9-03d2fbd19828"));
    receivableItem1.setSequence(1);
    receivableItem1.setSalesItemId("API_FIRM_PREMIUM");
    receivableItem1.setSourceId("API55415");
    receivableItem1.setSalesItemName("SalesItemName");
    receivableItem1.setQuantity(1D);
    receivableItem1.setUnitAmount(new BigDecimal("8.000000"));
    receivableItem1.setAmount(new BigDecimal("8.00"));
    receivableItem1.setSalesTax(new BigDecimal("0.000000"));
    receivableItem1.setTotal(new BigDecimal("8.00"));
    receivableItem1.setExtendedUnitAmt("8.0");
    receivableItem1.setBranchId(" ");
    receivableItem1.setFilingId(" ");
    receivableItem1.setIndividualCrdNo(" ");
    receivableItem1.setIndividualName(" ");
    receivableItem1.setBillingCode(" ");
    receivableItem1.setSourceTransId(" ");
    receivableItem1.setLineItemDescription("");
    receivableItem1.setProductCategory(" ");

    ReceivableItem receivableItem2 = new ReceivableItem();
    receivableItem2.setId(UUID.fromString("e95f92a8-2ace-4ca2-82e9-03d2fbd19828"));
    receivableItem2.setSequence(2);
    receivableItem2.setSalesItemId("API_FIRM_PREMIUM");
    receivableItem2.setSourceId("API55415");
    receivableItem2.setSalesItemName("SalesItemName");
    receivableItem2.setQuantity(2D);
    receivableItem2.setUnitAmount(new BigDecimal("6.000000"));
    receivableItem2.setAmount(new BigDecimal("12.00"));
    receivableItem2.setSalesTax(new BigDecimal("0.000000"));
    receivableItem2.setTotal(new BigDecimal("12.00"));
    receivableItem2.setExtendedUnitAmt("6.0");
    receivableItem2.setBranchId(" ");
    receivableItem2.setFilingId(" ");
    receivableItem2.setIndividualCrdNo(" ");
    receivableItem2.setIndividualName(" ");
    receivableItem2.setBillingCode(" ");
    receivableItem2.setSourceTransId(" ");
    receivableItem2.setLineItemDescription("");
    receivableItem2.setProductCategory(" ");
    receivable.setLines(Arrays.asList(receivableItem1, receivableItem2));
    jsonLines =
        gson.fromJson(FileReaderUtilTest.getResourceContent("ReceivableJson.json"), List.class);
  }

  @Test
  public void convertReceivableEntityToDtoTest() {
    Receivable receivable = receivableUtil.convertReceivableEntityToDto(receivableEntity);
    assertEquals(new BigDecimal(20.00).setScale(2), receivable.getAmount());
    assertEquals("2023-08-01T00:00:00", receivable.getInvoiceDate());
  }

  @Test
  public void convertReceivableEntityToDtoTestForAmountNull() {
    Receivable receivable =
        receivableUtil.convertReceivableEntityToDto(receivableEntityWithAmountNull);
    assertNull(receivable.getAmount());
  }

  @Test
  public void convertReceivableItemEntityToDtoTestSuccess() {
    String expectedEST = "2023-08-01T00:00:00";
    ReceivableItem receivableItem =
        receivableUtil.convertReceivableItemEntityToDto(
            receivableEntity.getReceivableItems().get(0), "API55415");
    assertEquals("API55415", receivableItem.getSourceId());
    assertEquals(expectedEST, receivableItem.getCrdTransactionDate());
    assertEquals(expectedEST, receivableItem.getFromDate());
    assertEquals(expectedEST, receivableItem.getToDate());
    assertEquals(expectedEST, receivableItem.getSourceTransDate());
    assertEquals(expectedEST, receivableItem.getCaseOpenDate());
  }

  @Test
  public void convertReceivableItemEntityToDtoTestNull() {
    ReceivableItemEntity rie = new ReceivableItemEntity();
    rie.setCrdTransactionDate(null);
    ReceivableItem receivableItem =
        receivableUtil.convertReceivableItemEntityToDto(rie, "API55415");
    assertEquals("API55415", receivableItem.getSourceId());
    assertNull(receivableItem.getCrdTransactionDate());
  }

  @Test
  public void getJsonLines() {
    List<String> finalLines = receivableUtil.getJsonLines(Arrays.asList(receivable));
    assertEquals(jsonLines, finalLines);
  }
}
