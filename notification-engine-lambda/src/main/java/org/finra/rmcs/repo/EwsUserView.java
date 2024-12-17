package org.finra.rmcs.repo;

import org.finra.rmcs.entity.EwsUserDetailViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public interface EwsUserView extends JpaRepository<EwsUserDetailViewEntity, String> {

  @Query(
      nativeQuery = true,
      value =
          "SELECT id, user_id, email_1_tx, crd_org_id, saa_actv_fl\n"
              + "FROM modeled.ews_user_dtl_vw where crd_org_id = ?1 and saa_actv_fl = 'Y'")
  EwsUserDetailViewEntity findByOrgId(@Param("orgId") Integer orgId);
}
