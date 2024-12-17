package org.finra.rmcs.repo;

import java.util.List;
import org.finra.rmcs.entity.SalesItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SalesItemRepo extends JpaRepository<SalesItemEntity, Integer> {

  @Query(
      value =
          "SELECT * FROM sales_item_catlog_vw sic WHERE sic.revenue_stream_name IN (:revenueStream)",
      nativeQuery = true)
  List<SalesItemEntity> findByRevenueStreams(List<String> revenueStream);
}
