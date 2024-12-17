package org.finra.rmcs.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.finra.rmcs.entity.Org;
import org.finra.rmcs.entity.PaymentTrackingEntity;
import org.finra.rmcs.repo.OrgRepo;
import org.finra.rmcs.service.impl.OrganizationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class OrganizationServiceImplTest {

  @Mock
  private OrgRepo orgRepo;
  @InjectMocks
  private OrganizationServiceImpl organizationService;

  @Test
  public void testGetOrganizationDetailsWithData() {
    String buUint = "CRDRG";
    List<PaymentTrackingEntity> paymentTrackingEntityList =
        new ArrayList<>();
    PaymentTrackingEntity list = new PaymentTrackingEntity();
    list.setOrgId("79");
    list.setOrgId("79");
    list.setCustomerId("99");
    paymentTrackingEntityList.add(list);
    List<String> orgIds = paymentTrackingEntityList.stream()
        .map(paymentTracking -> paymentTracking.getOrgId()).collect(
            Collectors.toList());
    List<String> customerIds = paymentTrackingEntityList.stream()
        .map(paymentTracking -> paymentTracking.getCustomerId()).collect(
            Collectors.toList());
    List<String> expectedOrgIds = Arrays.asList("79");
    List<String> expectedCustomerIds = Arrays.asList("99");
    Assertions.assertEquals(expectedOrgIds, orgIds);
    Assertions.assertEquals(expectedCustomerIds, customerIds);
    Map<String, String> orgMap = organizationService.getOrganizationDetailsByBUAndPaymentNumber(
        buUint, paymentTrackingEntityList);
  }

  @Test
  public void testGetOrganizationDetailsWithoutData() {
    String buUint = "null";
    List<PaymentTrackingEntity> paymentTrackingEntityList =
        new ArrayList<>();
    PaymentTrackingEntity list = new PaymentTrackingEntity();
    List<String> orgIds = paymentTrackingEntityList.stream()
        .map(paymentTracking -> paymentTracking.getOrgId()).collect(
            Collectors.toList());
    List<String> customerIds = paymentTrackingEntityList.stream()
        .map(paymentTracking -> paymentTracking.getCustomerId()).collect(
            Collectors.toList());
    Map<String, String> orgMap = organizationService.getOrganizationDetailsByBUAndPaymentNumber(
        buUint, paymentTrackingEntityList);
  }

  @Test
  public void testGetOrganizationDetailsWithOrgData() {
    String buUint = "CRDRG";
    List<PaymentTrackingEntity> paymentTrackingEntityList =
        new ArrayList<>();
    PaymentTrackingEntity list = new PaymentTrackingEntity();
    list.setOrgId("79");
    list.setOrgId("79");
    list.setCustomerId("99");
    paymentTrackingEntityList.add(list);
    List<Org> org = new ArrayList<>();
    Org orgid = new Org();
    orgid.setId(UUID.randomUUID());
    orgid.setCustomerId("abc");
    orgid.setName("abcd");
    org.add(orgid);
    List<String> orgIds = paymentTrackingEntityList.stream()
        .map(paymentTracking -> paymentTracking.getOrgId()).collect(
            Collectors.toList());
    List<String> customerIds = paymentTrackingEntityList.stream()
        .map(paymentTracking -> paymentTracking.getCustomerId()).collect(
            Collectors.toList());
    List<String> expectedOrgIds = Arrays.asList("79");
    List<String> expectedCustomerIds = Arrays.asList("99");
    Assertions.assertEquals(expectedOrgIds, orgIds);
    Assertions.assertEquals(expectedCustomerIds, customerIds);
    when(orgRepo.findBySetIdAndCustomerIdIn(any(), any())).thenReturn(org);
    Map<String, String> orgMap = organizationService.getOrganizationDetailsByBUAndPaymentNumber(
        buUint, paymentTrackingEntityList);
  }
}
