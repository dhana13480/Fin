package org.finra.rmcs.repo;

import java.util.List;
import java.util.UUID;
import org.finra.rmcs.entity.PaymentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PaymentStatusHistoryRepo extends JpaRepository<PaymentStatusHistory, UUID> {

  @Query(
      value = "SELECT * FROM payments.payment_status_history WHERE workday_notify_status_id = 2 AND created_timestamp between CAST (?1 AS timestamptz) and CAST (?2 AS timestamptz)",
      nativeQuery = true)
  List<PaymentStatusHistory> findStatusHistoryByDate(String startDateTime, String endDateTime);

}
