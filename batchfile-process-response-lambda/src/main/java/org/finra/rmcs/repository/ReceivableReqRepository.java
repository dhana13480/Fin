package org.finra.rmcs.repository;

import java.util.List;
import java.util.UUID;
import org.finra.rmcs.entity.ReceivableReqEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceivableReqRepository extends JpaRepository<ReceivableReqEntity, UUID> {

  @Query(
      nativeQuery = true,
      value =
          "SELECT CAST(response_payload AS text) FROM rmcs.receivable_req WHERE transmission_id = ?1 and status = ?2")
  List<String> findResponsePayloadByTransmissionIdAndStatus(String transmissionId, String status);
}
