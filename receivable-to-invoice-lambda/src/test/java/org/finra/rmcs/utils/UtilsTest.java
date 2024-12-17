package org.finra.rmcs.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.finra.rmcs.FileReaderUtilTest;
import org.finra.rmcs.common.constants.ReceivableStatus;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.entity.ReceivableEntity;
import org.finra.rmcs.entity.ReceivableItemEntity;
import org.finra.rmcs.entity.SalesItemEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class UtilsTest {

  private static ReceivableEntity receivableEntity;
  private static ReceivableEntity receivableEntity2;
  private static ReceivableEntity receivableEntity3;
  private Utils util;

  @BeforeAll
  public static void initReceivables() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    receivableEntity =
        objectMapper.readValue(
            FileReaderUtilTest.getResourceContent("ReceivablesRequest.json"),
            ReceivableEntity.class);
    receivableEntity2 =
        objectMapper.readValue(
            FileReaderUtilTest.getResourceContent("ReceivablesRequest.json"),
            ReceivableEntity.class);
    receivableEntity3 =
        objectMapper.readValue(
            FileReaderUtilTest.getResourceContent("MTRCSReceivablesRequest.json"),
            ReceivableEntity.class);
  }

  @BeforeEach
  public void init() {
    util = new Utils();
  }

  @Test
  public void updateReceivableTest() {
    SalesItemEntity item = new SalesItemEntity();
    item.setRevenueStream("receivable_APIBI");
    item.setSalesItemId("receivable_salesItem");
    item.setDefRevRuleId("RC5");
    List<SalesItemEntity> salesItemEntityList = List.of(item);
    ReceivableEntity updatedReceivableEntity =
        util.updateReceivable(receivableEntity, salesItemEntityList);
    assertEquals(Constants.UPDATED_BY, updatedReceivableEntity.getUpdatedBy());
    assertEquals(Constants.INVOICE_STATUS, updatedReceivableEntity.getInvoiceStatus());
    assertEquals(ReceivableStatus.INVOICED.getId(), updatedReceivableEntity.getStatus());
    assertEquals(
        new BigDecimal("17.630000"),
        updatedReceivableEntity.getReceivableItems().get(0).getTotal());
  }

  @Test
  public void updateReceivableTestWithDefRevRuleIdNull() {
    SalesItemEntity item = new SalesItemEntity();
    item.setRevenueStream("receivable_APIBI");
    item.setSalesItemId("receivable_salesItem");
    item.setDefRevRuleId(null);
    List<SalesItemEntity> salesItemEntityList = List.of(item);
    ReceivableEntity updatedReceivableEntity =
        util.updateReceivable(receivableEntity2, salesItemEntityList);
    assertEquals(Constants.UPDATED_BY, updatedReceivableEntity.getUpdatedBy());
    assertEquals(Constants.INVOICE_STATUS, updatedReceivableEntity.getInvoiceStatus());
    assertEquals(ReceivableStatus.INVOICED.getId(), updatedReceivableEntity.getStatus());
    assertEquals(
        new BigDecimal("17.630000"),
        updatedReceivableEntity.getReceivableItems().get(0).getTotal());
  }

  @Test
  public void updateReceivableBlankTest() {
    List<SalesItemEntity> salesItemEntityList = List.of(new SalesItemEntity());
    ReceivableEntity blankReceivableEntity = new ReceivableEntity();
    blankReceivableEntity.setReceivableItems(new ArrayList<>());
    ReceivableEntity updatedReceivableEntity =
        util.updateReceivable(blankReceivableEntity, salesItemEntityList);
    assertEquals("", updatedReceivableEntity.getClearingNumber());
  }

  @Test
  public void updateReceivableBlankTest2() {
    ReceivableEntity blankReceivableEntity = new ReceivableEntity();
    blankReceivableEntity.setReceivableItems(new ArrayList<>());
    blankReceivableEntity.setClearingNumber(null);
    blankReceivableEntity.setMpID(null);
    List<SalesItemEntity> salesItemEntityList = List.of(new SalesItemEntity());
    ReceivableEntity updatedReceivableEntity =
        util.updateReceivable(blankReceivableEntity, salesItemEntityList);
    assertEquals("", updatedReceivableEntity.getClearingNumber());
  }

  @Test
  public void updateReceivableItemBlankTest() {
    ReceivableItemEntity blankReceivableItemEntity = new ReceivableItemEntity();
    blankReceivableItemEntity.setAmount(new BigDecimal(1));
    blankReceivableItemEntity.setId(UUID.randomUUID());
    ReceivableItemEntity updatedReceivableItemEntity =
        util.updateReceivableItem(blankReceivableItemEntity);
    assertEquals("", updatedReceivableItemEntity.getBranchId());
    assertEquals(new BigDecimal(1), updatedReceivableItemEntity.getTotal());
  }

  @Test
  public void updateReceivableTestWithDefRevRuleIdMulti() {
    SalesItemEntity item = new SalesItemEntity();
    item.setRevenueStream("MTRCS");
    item.setSalesItemId("SESS");
    item.setDefRevRuleId(null);
    SalesItemEntity item2 = new SalesItemEntity();
    item2.setRevenueStream("MTRCS");
    item2.setSalesItemId("LPHCF");
    item2.setDefRevRuleId("RC6");
    SalesItemEntity item3 = new SalesItemEntity();
    item3.setRevenueStream("MTRCS");
    item3.setSalesItemId("ADM2");
    item3.setDefRevRuleId("");
    List<SalesItemEntity> salesItemEntityList = List.of(item, item2, item3);
    ReceivableEntity updatedReceivableEntity =
        util.updateReceivable(receivableEntity3, salesItemEntityList);
    assertEquals(Constants.UPDATED_BY, updatedReceivableEntity.getUpdatedBy());
    assertEquals(Constants.INVOICE_STATUS, updatedReceivableEntity.getInvoiceStatus());
    assertEquals(ReceivableStatus.INVOICED.getId(), updatedReceivableEntity.getStatus());
    assertEquals(
        new BigDecimal("17.630000"),
        updatedReceivableEntity.getReceivableItems().get(0).getTotal());
  }
}
