package org.finra.rmcs.repo;

import java.util.List;
import org.finra.rmcs.entity.ReceivableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

public interface ReceivableRepo extends JpaRepository<ReceivableEntity, Integer> {

  @Query(
      nativeQuery = true,
      value =
          "SELECT * FROM receivable WHERE status = 14 and revenue_stream IN (:revenueStream) ORDER BY created_date")
  @Nullable
  List<ReceivableEntity> getReadyToBillReceivableOrderByCreatedDateDesc(List<String> revenueStream);
}
