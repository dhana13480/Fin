package org.finra.rmcs.repo;

import java.util.List;
import org.finra.rmcs.entity.ConnectReplicationStatusEntity;
import org.finra.rmcs.entity.DataRefreshLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectReplicationStatusRepo extends JpaRepository<ConnectReplicationStatusEntity, Integer> {

  @Query(nativeQuery = true, value =  "select distinct status from crisp_connect_target.connect_replication_status crs where entity in \n"
      + "('balance','invoice','payment','supplier_invoice', 'supplier_payment', 'ar_transaction', 'customer', 'sales_item') and TO_CHAR(date(created), 'yyyy-mm-dd')=TO_CHAR(date(current_date), 'yyyy-mm-dd');")
  List<String> fetchStatusFromConnectReplicationStatus();

}
