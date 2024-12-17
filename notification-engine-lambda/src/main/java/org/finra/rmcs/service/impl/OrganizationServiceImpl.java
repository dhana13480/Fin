package org.finra.rmcs.service.impl;

import com.amazonaws.util.StringUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.entity.Org;
import org.finra.rmcs.entity.PaymentTrackingEntity;
import org.finra.rmcs.repo.OrgRepo;
import org.finra.rmcs.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

  @Autowired
  private OrgRepo orgRepo;
  @Override
  public Map<String, String> getOrganizationDetailsByBUAndPaymentNumber(String buUnit,
      List<PaymentTrackingEntity> paymentTrackingEntityList) {
    log.info("Start to getOrganizationDetailsByBUAndPaymentNumber for buUnit:{}, paymentTrackingEntityList size:{}", buUnit, paymentTrackingEntityList.size());
    if(StringUtils.isNullOrEmpty(buUnit) || paymentTrackingEntityList.isEmpty()){
      log.info("either buUnit empty or paymentTrackingEntityList is empty");
      return new HashMap<>();
    }
    List<String> orgIds = paymentTrackingEntityList.stream().map(PaymentTrackingEntity::getOrgId).collect(
        Collectors.toList());
    List<String> customerIds = paymentTrackingEntityList.stream().map(PaymentTrackingEntity::getAftToOrgId).collect(
        Collectors.toList());

    Set<String> orgIdTotalList = new HashSet<>();
    orgIdTotalList.addAll(orgIds);
    orgIdTotalList.addAll(customerIds);
    log.info("Set size:{}", orgIdTotalList.size());

    List<Org> orgEntityList = orgRepo.findBySetIdAndCustomerIdIn(buUnit, orgIdTotalList);
    if(!orgEntityList.isEmpty()){
      return orgEntityList.stream()
              .collect(Collectors.toMap(Org::getCustomerId,  Org::getName));
    }
    return new HashMap<>();
  }


}
