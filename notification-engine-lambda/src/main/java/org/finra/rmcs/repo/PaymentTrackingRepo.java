package org.finra.rmcs.repo;

import java.util.List;
import org.finra.rmcs.entity.PaymentTrackingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public interface PaymentTrackingRepo extends JpaRepository<PaymentTrackingEntity, String> {

    List<PaymentTrackingEntity> findByPaymentReferenceNumberIn(@Param("paymentReferenceNumber") List<String> paymentReferenceNumber);
}