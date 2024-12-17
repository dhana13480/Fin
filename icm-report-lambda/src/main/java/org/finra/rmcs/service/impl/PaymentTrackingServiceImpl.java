package org.finra.rmcs.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.ebillrepo.PaymentTrackingRepo;
import org.finra.rmcs.entity.PaymentTrackingEntity;
import org.finra.rmcs.entity.ReceivableAuditEntity;
import org.finra.rmcs.repo.PaymentRepo;
import org.finra.rmcs.repo.ReceivableAuditRepo;
import org.finra.rmcs.service.PaymentTrackingService;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class PaymentTrackingServiceImpl implements PaymentTrackingService {

    private final PaymentTrackingRepo paymentTrackingRepo;
    @Override
    public List<PaymentTrackingEntity> findByCreatedDate(ZonedDateTime entryDate) {
        String startDate =
                new StringBuilder()
                        .append("'")
                        .append(entryDate.toLocalDate())
                        .append(" 00:00:00 ")
                        .append(entryDate.getOffset())
                        .append("'")
                        .toString();
        String endDate =
                new StringBuilder()
                        .append("'")
                        .append(entryDate.toLocalDate())
                        .append(" 23:59:59 ")
                        .append(entryDate.getOffset())
                        .append("'")
                        .toString();
        log.info(
                "SQL Query: SELECT * FROM PaymentTrackingEntity pt where pymnt_type_id = 5" +
                        "and pt.send_to_wd = true and pt.created_date {} and {} order by pt.created_date asc",
                startDate,
                endDate);
        return paymentTrackingRepo.findByCreatedDate(startDate,endDate);

    }
}
