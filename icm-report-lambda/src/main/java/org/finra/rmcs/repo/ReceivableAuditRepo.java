package org.finra.rmcs.repo;

import java.util.List;
import org.finra.rmcs.entity.ReceivableAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ReceivableAuditRepo extends JpaRepository<ReceivableAuditEntity, Integer> {

  @Query(
      value =
          "SELECT * FROM receivable_audit ra where ra.audit_entry_created_date between CAST (?1 AS timestamptz) and CAST (?2 AS timestamptz) order by ra.audit_entry_created_date asc",
      nativeQuery = true)
  List<ReceivableAuditEntity> findByAuditEntryCreatedDate(String startDateTime, String endDateTime);
}
