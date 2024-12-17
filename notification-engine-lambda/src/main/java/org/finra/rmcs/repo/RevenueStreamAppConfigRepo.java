package org.finra.rmcs.repo;

import org.finra.rmcs.entity.PaymentTrackingEntity;
import org.finra.rmcs.entity.RevenueStreamAppConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public interface RevenueStreamAppConfigRepo extends JpaRepository<RevenueStreamAppConfigEntity, String> {

    @Query("select rs.revenueStreamNameDesc from RevenueStreamAppConfigEntity rs where rs.revenueStreamName = :revenueStreamNameDesc")
    String findbyRevenueStreamNameDesc(String revenueStreamNameDesc);
}
