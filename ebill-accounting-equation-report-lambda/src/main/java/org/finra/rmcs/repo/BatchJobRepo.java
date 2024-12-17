package org.finra.rmcs.repo;

import java.util.UUID;
import org.finra.rmcs.entity.BatchJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchJobRepo extends JpaRepository<BatchJobEntity, UUID> {

  @Query(
      nativeQuery = true,
      value =
          "select * from  ebill.batch_job_tracking bjt where name=:name and TO_CHAR(date(start_date at time zone 'EST'),\n"
              + "             'yyyy-mm-dd')= TO_CHAR(date(current_timestamp at time zone 'EST'),\n"
              + "             'yyyy-mm-dd') order by end_date desc limit 1")
  BatchJobEntity findByName(@Param("name") String name);
}
