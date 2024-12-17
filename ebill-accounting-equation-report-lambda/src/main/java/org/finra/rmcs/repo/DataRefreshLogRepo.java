package org.finra.rmcs.repo;

import org.finra.rmcs.entity.DataRefreshLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DataRefreshLogRepo extends JpaRepository<DataRefreshLogEntity, Integer> {

  @Query(nativeQuery = true, value =  "select * from modeled.data_refresh_log drl where refresh_task_name = 'Main Refresh Task' and TO_CHAR(date(end_time), 'yyyy-mm-dd')=TO_CHAR(date(current_date), 'yyyy-mm-dd') ORDER BY start_time DESC LIMIT 1")
  DataRefreshLogEntity fetchDataRefreshLog();

}
