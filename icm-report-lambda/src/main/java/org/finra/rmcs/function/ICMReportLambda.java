package org.finra.rmcs.function;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.entity.PaymentEntity;
import org.finra.rmcs.entity.PaymentStatusHistory;
import org.finra.rmcs.repo.PaymentRepo;
import org.finra.rmcs.repo.PaymentStatusHistoryRepo;
import org.finra.rmcs.service.IcmReportService;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ICMReportLambda implements Function<Map<String, Object>, Map<String, Object>> {
    @NonNull private final IcmReportService icmReportService;

    private final PaymentStatusHistoryRepo paymentStatusHistoryRepo;
    private final PaymentRepo paymentRepo;

    @Override
    public Map<String, Object> apply(Map<String, Object> input) {
        UUID uuid = UUID.randomUUID();
        String correlationId = uuid.toString();
        String methodName =
            new StringBuilder()
                .append(Constants.CLASS)
                .append(this.getClass().getSimpleName())
                .append(" ")
                .append(Constants.METHOD)
                .append(Thread.currentThread().getStackTrace()[1].getMethodName())
                .append(" ")
                .append(Constants.CORRELATION_ID)
                .append(correlationId)
                .toString();
        log.info("{} message: method entry", methodName);
        log.info("icm slam lambda starts with the input:\n{}", input);

        boolean dryRun =
            Boolean.parseBoolean(input.getOrDefault(Constants.DRY_RUN, Constants.FALSE).toString());
        log.info("dryRun: {}", dryRun);
        if (dryRun) {
            log.info("dry run mode");
            input.put(Constants.DRY_RUN, Constants.SUCCESS);
            return input;
        }
        String reportDate = input.get(Constants.REPORT_DATE).toString();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        ZonedDateTime icmReportDate = null;
        if (StringUtils.isBlank(reportDate)) {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            ZoneId timeZone = ZoneId.of("America/New_York");
            icmReportDate = ZonedDateTime.of(yesterday.atStartOfDay(), timeZone);
        } else {
            LocalDate localReportDate = LocalDate.parse(reportDate, dateTimeFormatter);
            ZoneId timeZone = ZoneId.of("America/New_York");
            icmReportDate = ZonedDateTime.of(localReportDate.atStartOfDay(), timeZone);
        }
        boolean isEmailsent = icmReportService.sendICMReportAsEmail(icmReportDate);
        log.info("Icm report process completed {}", isEmailsent);
        return input;
    }
}
