package org.finra.rmcs.repo;

import java.util.List;
import org.finra.rmcs.entity.RevenueStreamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

public interface ReceivableRevenueStreamRepo extends JpaRepository<RevenueStreamEntity, String> {
  RevenueStreamEntity findByRevenueStreamName(String revenueStreamName);

  @Query(
      nativeQuery = true,
      value = "SELECT revenue_stream_name FROM revenue_stream WHERE send_to_wd = true")
  @Nullable
  List<String> findByRevenueStreamNameAndSendToWD();
}
