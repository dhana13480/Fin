package org.finra.rmcs.repo;

import org.finra.rmcs.entity.SalesItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SalesItemRepo extends JpaRepository<SalesItemEntity, Integer> {

  @Query(
      value =
          "SELECT * FROM sales_item_catlog_vw sic WHERE sic.sales_item_id = ?1 and sic.revenue_stream_name = ?2",
      nativeQuery = true)
  SalesItemEntity findBySalesItemAndRevenueStream(String salesItemId, String revenueStream);
}
