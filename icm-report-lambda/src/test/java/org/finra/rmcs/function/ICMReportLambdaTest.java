package org.finra.rmcs.function;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.service.IcmReportService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class ICMReportLambdaTest {

  @Mock
  private IcmReportService icmReportService;
  @InjectMocks
  private ICMReportLambda icmReportLambda;

  @Test
  public void testDryRun() {
    Map<String, Object> input = new HashMap<>();
    input.put(Constants.DRY_RUN, Constants.TRUE);
    Assertions.assertEquals(Constants.SUCCESS, icmReportLambda.apply(input).get(Constants.DRY_RUN));
  }

  @Test
  public void testBlankReportDate() {
    Map<String, Object> input = new HashMap<>();
    input.put(Constants.REPORT_DATE, "");
    LocalDate yesterday = LocalDate.now().minusDays(1);
    ZoneId timeZone = ZoneId.of("America/New_York");
    ZonedDateTime yesterdayWithTimeZone = ZonedDateTime.of(yesterday.atStartOfDay(), timeZone);
    Assertions.assertEquals(input, icmReportLambda.apply(input));
    verify(icmReportService, times(1)).sendICMReportAsEmail(yesterdayWithTimeZone);
  }

  @Test
  public void testReportDate() {
    Map<String, Object> input = new HashMap<>();
    input.put(Constants.REPORT_DATE, "06-30-2023");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    LocalDate yesterday = LocalDate.parse("06-30-2023", dateTimeFormatter);
    ZoneId timeZone = ZoneId.of("America/New_York");
    ZonedDateTime dateWithTimeZone = ZonedDateTime.of(yesterday.atStartOfDay(), timeZone);
    Assertions.assertEquals(input, icmReportLambda.apply(input));
    verify(icmReportService, times(1)).sendICMReportAsEmail(dateWithTimeZone);
  }

}
