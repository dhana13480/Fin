package org.finra.rmcs.repo;

import java.util.UUID;
import org.finra.rmcs.entity.EmailTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailTrackingRepo extends JpaRepository<EmailTracking, UUID> {

}
