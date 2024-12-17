package org.finra.rmcs.repo;

import java.util.List;
import org.finra.rmcs.entity.ReceivableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceivableRepo extends JpaRepository<ReceivableEntity, Integer> {

  // Used to validate given invoice ids in database
  @Query(value = "SELECT DISTINCT rc.invoice_id FROM receivable rc WHERE rc.invoice_id IN ( ?1 )",
      nativeQuery = true)
  List<String> findByInvoiceIdIn(List<String> invoiceIds);

  @Query(
      value = "SELECT * FROM receivable rc WHERE rc.invoice_id IN ( ?1 ) AND rc.status <> ?2 AND rc.status <> ?3",
      nativeQuery = true)
  List<ReceivableEntity> findValidReceivablesByInvoiceIds(List<String> invoiceIds,
      Integer invalidCode, Integer suspendCode);

}
