package org.finra.rmcs.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor
public class DeferredRevenueUtil {

  public static final String RC1 = "RC1";
  public static final String RC2 = "RC2";
  public static final String RC3 = "RC3";
  public static final String RC4 = "RC4";
  public static final String RC5 = "RC5";
  public static final String RC6 = "RC6";
  public static final String RC7 = "RC7";
  public static final String TO_DATE = "TO_DATE";
  public static final String FROM_DATE = "FROM_DATE";

  public static final String UTC_TIME_ZONE = "UTC";

  public static Map<String, String> deferredRevenueRulesCalc(
      LocalDateTime inputDateTime, String defRevRuleId) {
    Map<String, String> dateMap = new HashMap<>();
    try {
      switch (defRevRuleId) {
        case RC1:
          {
            dateMap = calcRC1(inputDateTime);
            break;
          }
        case RC2:
          {
            dateMap = calcRC2(inputDateTime);
            break;
          }
        case RC3:
          {
            dateMap = calcRC3(inputDateTime);
            break;
          }
        case RC4:
          {
            dateMap = calcRC4(inputDateTime);
            break;
          }
        case RC5:
          {
            dateMap = calcRC5(inputDateTime);
            break;
          }
        case RC6:
          {
            dateMap = calcRC6(inputDateTime);
            break;
          }
        case RC7:
          {
            dateMap = calcRC7(inputDateTime);
            break;
          }
        default:
          {
            dateMap.put(FROM_DATE, null);
            dateMap.put(TO_DATE, null);
          }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return dateMap;
  }

  private static Map<String, String> calcRC1(LocalDateTime inputDateTime) {
    ZonedDateTime zoned = convertToEST(inputDateTime);
    Map<String, String> returnMap = new HashMap<>();
    if (zoned.getMonthValue() < 12) {
      LocalDate fromDate = LocalDate.of(zoned.getYear(), zoned.getMonth(), 1);
      LocalDate toDate = LocalDate.of(zoned.getYear(), Month.DECEMBER, 31);
      returnMap.put(FROM_DATE, fromDate.toString());
      returnMap.put(TO_DATE, toDate.toString());
    } else {
      LocalDate fromDate = LocalDate.of(zoned.getYear() + 1, Month.JANUARY, 1);
      LocalDate toDate = LocalDate.of(zoned.getYear() + 1, Month.DECEMBER, 31);
      returnMap.put(FROM_DATE, fromDate.toString());
      returnMap.put(TO_DATE, toDate.toString());
    }
    return returnMap;
  }

  private static Map<String, String> calcRC2(LocalDateTime inputDateTime) {
    ZonedDateTime zoned = convertToEST(inputDateTime);
    Map<String, String> returnMap = new HashMap<>();
    if (zoned.getMonthValue() < 11) {
      LocalDate fromDate = LocalDate.of(zoned.getYear(), zoned.getMonth(), 1);
      LocalDate toDate = LocalDate.of(zoned.getYear(), Month.DECEMBER, 31);
      returnMap.put(FROM_DATE, fromDate.toString());
      returnMap.put(TO_DATE, toDate.toString());
    } else if (zoned.getMonthValue() == 11) {
      LocalDate fromDate = LocalDate.of(zoned.getYear(), Month.NOVEMBER, 1);
      LocalDate toDate = LocalDate.of(zoned.getYear(), Month.DECEMBER, 31);
      returnMap.put(FROM_DATE, fromDate.toString());
      returnMap.put(TO_DATE, toDate.toString());
    } else {
      LocalDate fromDate = LocalDate.of(zoned.getYear(), Month.DECEMBER, 1);
      LocalDate toDate = LocalDate.of(zoned.getYear(), Month.DECEMBER, 31);
      returnMap.put(FROM_DATE, fromDate.toString());
      returnMap.put(TO_DATE, toDate.toString());
    }
    return returnMap;
  }

  private static Map<String, String> calcRC3(LocalDateTime inputDateTime) {
    ZonedDateTime zoned = convertToEST(inputDateTime);
    Map<String, String> returnMap = new HashMap<>();
    if (zoned.getMonthValue() < 11) {
      LocalDate fromDate = LocalDate.of(zoned.getYear(), zoned.getMonth(), 1);
      LocalDate toDate =
          LocalDate.of(
              zoned.getYear(),
              zoned.getMonth().getValue() + 2,
              Month.of(zoned.getMonth().getValue() + 2).length(Year.of(zoned.getYear() + 1).isLeap()));
      returnMap.put(FROM_DATE, fromDate.toString());
      returnMap.put(TO_DATE, toDate.toString());
    } else if (zoned.getMonthValue() == 11) {
      LocalDate fromDate = LocalDate.of(zoned.getYear(), Month.NOVEMBER, 1);
      LocalDate toDate = LocalDate.of(zoned.getYear() + 1, Month.JANUARY, 31);
      returnMap.put(FROM_DATE, fromDate.toString());
      returnMap.put(TO_DATE, toDate.toString());
    } else if (zoned.getMonthValue() == 12) {
      LocalDate fromDate = LocalDate.of(zoned.getYear(), Month.DECEMBER, 1);
      LocalDate toDate =
          LocalDate.of(
              zoned.getYear() + 1,
              Month.FEBRUARY,
              Month.FEBRUARY.length(Year.of(zoned.getYear() + 1).isLeap()));
      returnMap.put(FROM_DATE, fromDate.toString());
      returnMap.put(TO_DATE, toDate.toString());
    }
    return returnMap;
  }

  private static Map<String, String> calcRC4(LocalDateTime inputDateTime) {
    ZonedDateTime zoned = convertToEST(inputDateTime);
    Map<String, String> returnMap = new HashMap<>();
    LocalDate fromDate = LocalDate.of(zoned.getYear(), Month.JANUARY, 1);
    LocalDate toDate = LocalDate.of(zoned.getYear(), Month.DECEMBER, 31);
    returnMap.put(FROM_DATE, fromDate.toString());
    returnMap.put(TO_DATE, toDate.toString());
    return returnMap;
  }

  private static Map<String, String> calcRC5(LocalDateTime inputDateTime) {
    ZonedDateTime zoned = convertToEST(inputDateTime);
    Map<String, String> returnMap = new HashMap<>();
    LocalDate updateToDate = zoned.plusMonths(13).toLocalDate();
    LocalDate fromDate = LocalDate.of(zoned.getYear(), zoned.getMonth(), 1);
    LocalDate toDate =
        LocalDate.of(
            updateToDate.getYear(),
            updateToDate.getMonth(),
            updateToDate.getMonth().length(Year.of(zoned.getYear() + 1).isLeap()));
    returnMap.put(FROM_DATE, fromDate.toString());
    returnMap.put(TO_DATE, toDate.toString());
    return returnMap;
  }

  private static Map<String, String> calcRC6(LocalDateTime inputDateTime) {
    ZonedDateTime zoned = convertToEST(inputDateTime);
    Map<String, String> returnMap = new HashMap<>();
    LocalDate updateToDate = zoned.plusMonths(3).toLocalDate();
    LocalDate fromDate = LocalDate.of(zoned.getYear(), zoned.getMonth(), 1);
    LocalDate toDate =
        LocalDate.of(
            updateToDate.getYear(),
            updateToDate.getMonth(),
            updateToDate.getMonth().length(Year.of(zoned.getYear() + 1).isLeap()));
    returnMap.put(FROM_DATE, fromDate.toString());
    returnMap.put(TO_DATE, toDate.toString());
    return returnMap;
  }

  private static Map<String, String> calcRC7(LocalDateTime inputDateTime) {
    ZonedDateTime zoned = convertToEST(inputDateTime);
    Map<String, String> returnMap = new HashMap<>();
    if (zoned.getMonthValue() == 12) {

      LocalDate fromDate = LocalDate.of(zoned.getYear() + 1, Month.JANUARY, 1);
      LocalDate toDate = LocalDate.of(zoned.getYear() + 1, Month.DECEMBER, 31);
      returnMap.put(FROM_DATE, fromDate.toString());
      returnMap.put(TO_DATE, toDate.toString());
    } else {
      LocalDate fromDate = LocalDate.of(zoned.getYear(), zoned.getMonth(), 1);
      LocalDate toDate = LocalDate.of(zoned.getYear(), Month.DECEMBER, 31);
      returnMap.put(FROM_DATE, fromDate.toString());
      returnMap.put(TO_DATE, toDate.toString());
    }
    return returnMap;
  }

  public static ZonedDateTime convertToEST(LocalDateTime localDateTime) {
    ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(UTC_TIME_ZONE));
    return zonedDateTime.withZoneSameInstant(ZoneId.of(Constants.TIME_ZONE_AMERICA_NEW_YORK));
  }

  public static boolean isCaseOpenDate(String defRevRuleId) {
    return StringUtils.equalsAnyIgnoreCase(defRevRuleId, RC5, RC6);
  }
}
