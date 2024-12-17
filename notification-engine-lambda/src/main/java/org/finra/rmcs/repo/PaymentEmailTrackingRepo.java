package org.finra.rmcs.repo;

import java.util.UUID;
import org.finra.rmcs.entity.PaymentEmailTracking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentEmailTrackingRepo extends JpaRepository<PaymentEmailTracking, UUID> {

}
