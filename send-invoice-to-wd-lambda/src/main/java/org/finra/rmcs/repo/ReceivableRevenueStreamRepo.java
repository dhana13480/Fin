package org.finra.rmcs.repo;

import org.finra.rmcs.entity.RevenueStreamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceivableRevenueStreamRepo extends JpaRepository<RevenueStreamEntity, String> {}
