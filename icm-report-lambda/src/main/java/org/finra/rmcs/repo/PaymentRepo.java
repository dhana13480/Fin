package org.finra.rmcs.repo;

import java.util.List;
import java.util.UUID;
import org.finra.rmcs.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends JpaRepository<PaymentEntity, UUID> {

  @Query(nativeQuery = true, value = "SELECT * FROM payments.payment WHERE id in (?1)")
  List<PaymentEntity> findByIds(List<UUID> paymentIds);
}
