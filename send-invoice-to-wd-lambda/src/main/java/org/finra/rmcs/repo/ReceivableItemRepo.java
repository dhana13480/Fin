package org.finra.rmcs.repo;

import java.util.List;
import java.util.UUID;
import org.finra.rmcs.entity.ReceivableItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

public interface ReceivableItemRepo extends JpaRepository<ReceivableItemEntity, Integer> {

  @Query(nativeQuery = true, value = "SELECT * FROM receivable_item WHERE id = ?1")
  @Nullable
  List<ReceivableItemEntity> findByReceivableId(UUID receivableId);
}
