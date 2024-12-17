package org.finra.rmcs.repository;

import java.util.Optional;
import java.util.UUID;
import org.finra.rmcs.entity.ReceivableJsonFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceivableJsonRepository extends JpaRepository<ReceivableJsonFileEntity, UUID> {

  @Query(
      nativeQuery = true,
      value = "SELECT * FROM rmcs.receivable_json_file WHERE transmission_id = ?1")
  Optional<ReceivableJsonFileEntity> findByTransmissionId(String transmissionId);
}
