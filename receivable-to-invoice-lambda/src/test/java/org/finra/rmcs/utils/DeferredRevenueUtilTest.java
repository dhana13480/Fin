package org.finra.rmcs.utils;

import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeferredRevenueUtilTest {

  DeferredRevenueUtil deferredRevenueUtil;

  @BeforeEach
  public void init() {
    deferredRevenueUtil = new DeferredRevenueUtil();
  }

  @Test
  public void testForIsCaseOpenDateFalse() {
    Assertions.assertFalse(DeferredRevenueUtil.isCaseOpenDate("RC1"));
  }

  @Test
  public void testForIsCaseOpenDateTrue() {
    Assertions.assertTrue(DeferredRevenueUtil.isCaseOpenDate("RC5"));
  }

  @Test
  public void testForRC1ForOtherThanDecember() {
    String inputDateTime = "2023-03-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC1");
    Assertions.assertEquals("2023-03-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2023-12-31", res.get("TO_DATE"));
  }

  @Test
  public void testForRC1ForDecember() {
    String inputDateTime = "2023-12-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC1");
    Assertions.assertEquals("2024-01-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2024-12-31", res.get("TO_DATE"));
  }

  @Test
  public void testForRC2ForJanuary() {
    String inputDateTime = "2023-03-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC2");
    Assertions.assertEquals("2023-03-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2023-12-31", res.get("TO_DATE"));
  }

  @Test
  public void testForRC2ForNovember() {
    String inputDateTime = "2023-11-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC2");
    Assertions.assertEquals("2023-11-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2023-12-31", res.get("TO_DATE"));
  }

  @Test
  public void testForRC2ForDecember() {
    String inputDateTime = "2023-12-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC2");
    Assertions.assertEquals("2023-12-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2023-12-31", res.get("TO_DATE"));
  }

  @Test
  public void testForRC3ForJanuary() {
    String inputDateTime = "2023-01-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC3");
    Assertions.assertEquals("2023-01-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2023-03-31", res.get("TO_DATE"));
  }

  @Test
  public void testForRC3ForApril() {
    String inputDateTime = "2023-04-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC3");
    Assertions.assertEquals("2023-04-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2023-06-30", res.get("TO_DATE"));
  }

  @Test
  public void testForRC3ForOctober() {
    String inputDateTime = "2023-10-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC3");
    Assertions.assertEquals("2023-10-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2023-12-31", res.get("TO_DATE"));
  }

  @Test
  public void testForRC3ForNovember() {
    String inputDateTime = "2023-11-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC3");
    Assertions.assertEquals("2023-11-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2024-01-31", res.get("TO_DATE"));
  }

  @Test
  public void testForRC3ForDecember() {
    String inputDateTime = "2023-12-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC3");
    Assertions.assertEquals("2023-12-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2024-02-29", res.get("TO_DATE"));
  }

  @Test
  public void testForRC4ForMarch() {
    String inputDateTime = "2024-03-16T00:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC4");
    Assertions.assertEquals("2024-01-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2024-12-31", res.get("TO_DATE"));
  }

  @Test
  public void testForRC4ForJanuary() {
    String inputDateTime = "2025-01-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC4");
    Assertions.assertEquals("2025-01-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2025-12-31", res.get("TO_DATE"));
  }

  @Test
  public void testForRC5ForJanuary20() {
    String inputDateTime = "2020-01-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC5");
    Assertions.assertEquals("2020-01-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2021-02-28", res.get("TO_DATE"));
  }

  @Test
  public void testForRC5ForNovember12() {
    String inputDateTime = "2012-11-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC5");
    Assertions.assertEquals("2012-11-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2013-12-31", res.get("TO_DATE"));
  }

  @Test
  public void testForRC5ForNovember24() {
    String inputDateTime = "2024-11-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC5");
    Assertions.assertEquals("2024-11-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2025-12-31", res.get("TO_DATE"));
  }

  @Test
  public void testForRC6ForJanuary() {
    String inputDateTime = "2020-01-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC6");
    Assertions.assertEquals("2020-01-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2020-04-30", res.get("TO_DATE"));
  }

  @Test
  public void testForRC6ForNovember12() {
    String inputDateTime = "2012-11-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC6");
    Assertions.assertEquals("2012-11-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2013-02-28", res.get("TO_DATE"));
  }

  @Test
  public void testForRC6ForNovember24() {
    String inputDateTime = "2024-11-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC6");
    Assertions.assertEquals("2024-11-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2025-02-28", res.get("TO_DATE"));
  }

  @Test
  public void testForRC7ForJanuary() {
    String inputDateTime = "2024-01-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC7");
    Assertions.assertEquals("2024-01-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2024-12-31", res.get("TO_DATE"));
  }

  @Test
  public void testForRC7ForDecember() {
    String inputDateTime = "2024-12-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC7");
    Assertions.assertEquals("2025-01-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2025-12-31", res.get("TO_DATE"));
  }

  @Test
  public void testForRC7ForFebruary() {
    String inputDateTime = "2025-02-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "RC7");
    Assertions.assertEquals("2025-02-01", res.get("FROM_DATE"));
    Assertions.assertEquals("2025-12-31", res.get("TO_DATE"));
  }

  @Test
  public void testForDefault() {
    String inputDateTime = "2023-10-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Map<String, String> res = DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, "");
    Assertions.assertNull(res.get("FROM_DATE"));
    Assertions.assertNull(res.get("TO_DATE"));
  }

  @Test
  public void testForException() {
    String inputDateTime = "2023-10-16T14:19:24.242704";
    LocalDateTime localDateTime = LocalDateTime.parse(inputDateTime);
    Assertions.assertThrows(
        RuntimeException.class,
        () -> DeferredRevenueUtil.deferredRevenueRulesCalc(localDateTime, null));
  }
}
