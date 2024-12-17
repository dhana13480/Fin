package org.finra.rmcs.repo;

import java.util.UUID;
import org.finra.rmcs.entity.PaymentTrackingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public interface PaymentTrackingRepo extends JpaRepository<PaymentTrackingEntity, UUID> {

}