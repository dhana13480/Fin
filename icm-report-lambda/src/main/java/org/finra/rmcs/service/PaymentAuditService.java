package org.finra.rmcs.service;

import java.time.ZonedDateTime;
import java.util.List;
import org.finra.rmcs.dto.PaymentReport;

public interface PaymentAuditService {

  List<PaymentReport> findByCreatedDate(ZonedDateTime entryDate);
}
