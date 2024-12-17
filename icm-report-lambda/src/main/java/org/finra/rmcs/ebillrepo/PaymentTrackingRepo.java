package org.finra.rmcs.ebillrepo;

import org.finra.rmcs.entity.PaymentTrackingEntity;
import org.finra.rmcs.entity.ReceivableAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Component
public interface PaymentTrackingRepo extends JpaRepository<PaymentTrackingEntity, String> {


  /**
   * '@Author' : Sridhar
   * '@Method' Description: This method will return PaymentTrackingEntity collection for AFT
   * @return List<PaymentTrackingEntity> : collection of PaymentTrackingEntity
   */
  @Query( value = "SELECT * FROM EBILL.PAYMENT_TRACKING pt where pt.pymnt_type_id in(5,3) and pt.send_to_wd = true and pt.created_date between CAST (?1 AS timestamptz) and CAST (?2 AS timestamptz) order by pt.created_date asc",
          nativeQuery = true)
  List<PaymentTrackingEntity> findByCreatedDate(String startDateTime, String endDateTime);

}
