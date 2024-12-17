package org.finra.rmcs.service;

import java.util.List;
import java.util.Map;
import org.finra.rmcs.entity.PaymentTrackingEntity;

public interface OrganizationService {

  Map<String, String> getOrganizationDetailsByBUAndPaymentNumber(String buUnit, List<PaymentTrackingEntity> paymentTrackingEntityList);
}
