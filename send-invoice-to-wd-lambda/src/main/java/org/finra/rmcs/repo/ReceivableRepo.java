package org.finra.rmcs.repo;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.finra.rmcs.entity.ReceivableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ReceivableRepo extends JpaRepository<ReceivableEntity, Integer> {

  @Query(
      nativeQuery = true,
      value =
          "SELECT recv.* FROM rmcs.receivable recv "
              + "INNER JOIN (SELECT invoice_id,transmission_id, MAX(created_date) AS MaxDateTime "
              + "FROM rmcs.receivable where status = 15 and revenue_stream= ?1 "
              + "GROUP BY invoice_id,transmission_id ) groupedrecv ON recv.invoice_id = groupedrecv.invoice_id AND recv.transmission_id = groupedrecv.transmission_id AND recv.created_date = groupedrecv.MaxDateTime "
              + "WHERE status = 15 and revenue_stream= ?1")
  @Nullable
  List<ReceivableEntity> getInvoicedReceivable(String revenueStream);

  @Modifying
  @Query(
      nativeQuery = true,
      value = "UPDATE receivable SET status = 16 WHERE id IN ?1 AND status= 15")
  int updateReceivablesFromInvoicedToSendToWD(Set<UUID> receivableId);
}
