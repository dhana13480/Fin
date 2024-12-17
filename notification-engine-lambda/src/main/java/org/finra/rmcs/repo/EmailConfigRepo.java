package org.finra.rmcs.repo;

import org.finra.rmcs.entity.EmailConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public interface EmailConfigRepo extends JpaRepository<EmailConfig, String> {

    EmailConfig findByEventTypeName(@Param("eventTypeName") String eventTypeName);
}