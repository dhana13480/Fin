package org.finra.rmcs.repo;

import org.finra.rmcs.entity.AlertEmailEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Component
public interface AlertEmailEventRepo extends JpaRepository<AlertEmailEventEntity, UUID> {

    @Query("select ae from AlertEmailEventEntity ae where ae.invoiceNumber IN (:invoiceNumber) and ae.EmailTypeName = :EmailTypeName")
    List<AlertEmailEventEntity> findByInvoiceId( List<String> invoiceNumber,String EmailTypeName);

}