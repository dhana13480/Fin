package org.finra.rmcs.repo;

import java.util.Optional;
import java.util.UUID;
import org.finra.rmcs.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public interface SubscriptionRepo extends JpaRepository<SubscriptionEntity, UUID> {
}
