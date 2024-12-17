package org.finra.rmcs.repo;

import java.util.UUID;
import org.finra.rmcs.entity.EbillBatchSchedulerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EbillBatchSchedulerRepo extends JpaRepository<EbillBatchSchedulerEntity, UUID> {

  @Query(nativeQuery = true, value =  "select * from modeled.ebill_batch_scheduler ebs where TO_CHAR(date(start_time), 'yyyy-mm-dd')=TO_CHAR(date(current_date), 'yyyy-mm-dd') ORDER BY start_time DESC LIMIT 1")
  EbillBatchSchedulerEntity fetchEbillBatchScheduler();

}
