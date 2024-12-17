package org.finra.rmcs.repo;

import java.util.List;
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
      value = "SELECT * FROM receivable WHERE status NOT IN (12,13) and invoice_id in (:invoiceIds)")
  @Nullable
  List<ReceivableEntity> getReceivable(List<String> invoiceIds);

  @Modifying
  @Query(nativeQuery = true, value = "UPDATE receivable SET payment_received = (:paymentReceived) WHERE id in (:ids)")
  int updateReceivablesFromInvoicedToSendToWD(String paymentReceived, List<UUID> ids);
}
