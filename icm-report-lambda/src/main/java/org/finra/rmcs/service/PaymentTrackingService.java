package org.finra.rmcs.service;

import org.finra.rmcs.dto.ReceivableAudit;
import org.finra.rmcs.entity.PaymentTrackingEntity;

import java.time.ZonedDateTime;
import java.util.List;

public interface PaymentTrackingService {

    List<PaymentTrackingEntity> findByCreatedDate(ZonedDateTime entryDate);

}
