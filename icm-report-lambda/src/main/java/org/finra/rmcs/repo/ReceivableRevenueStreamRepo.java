package org.finra.rmcs.repo;

import java.util.List;
import org.finra.rmcs.entity.RevenueStreamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ReceivableRevenueStreamRepo extends JpaRepository<RevenueStreamEntity, String> {

  @Query(
      value = "SELECT rs.revenue_stream_name FROM revenue_stream rs WHERE rs.active = true",
      nativeQuery = true)
  List<String> findActiveRevenueStream();
}
