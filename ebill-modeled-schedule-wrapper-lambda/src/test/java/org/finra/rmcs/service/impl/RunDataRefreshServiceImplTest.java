package org.finra.rmcs.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.finra.rmcs.dto.EbillRunDataRefreshResponse;
import org.finra.rmcs.entity.DataRefreshLogEntity;
import org.finra.rmcs.repo.ConnectReplicationStatusRepo;
import org.finra.rmcs.repo.DataRefreshLogRepo;
import org.finra.rmcs.repo.EbillBatchSchedulerRepo;
import org.finra.rmcs.service.EwsTokenservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@SpringJUnitConfig
public class RunDataRefreshServiceImplTest {

 @InjectMocks
 RunDataRefreshServiceImpl runDataRefreshServiceImpl;
 @Mock
 DataRefreshLogRepo dataRefreshLogRepo;
 @Mock
 ConnectReplicationStatusRepo connectReplicationStatusRepo;
 @Mock
 EbillBatchSchedulerRepo ebillBatchSchedulerRepo;
 @Mock
 NotificationServiceImpl notificationService;
 @Mock
 EwsTokenservice ewsTokenService;
 @Mock
 private RestTemplate restTemplate;

 @BeforeEach
 void setUp() {
  ReflectionTestUtils.setField(runDataRefreshServiceImpl, "runDataRefreshUrl", "http://localhost/api");
  ReflectionTestUtils.setField(runDataRefreshServiceImpl, "runDataRefreshAlert1", "04-30");
  ReflectionTestUtils.setField(runDataRefreshServiceImpl, "runDataRefreshAlert2", "07-00");
 }

 @Test
 public void testExecute() {
  List<String> status = Arrays.asList("Valid", "Valid");
  when(connectReplicationStatusRepo.fetchStatusFromConnectReplicationStatus()).thenReturn(status);
  EbillRunDataRefreshResponse response = EbillRunDataRefreshResponse.builder().message("Success").build();
  ResponseEntity<EbillRunDataRefreshResponse> responseEntity = ResponseEntity.status(
      HttpStatus.OK).body(response);
  when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
      eq(EbillRunDataRefreshResponse.class))).thenReturn(responseEntity);
  String res = runDataRefreshServiceImpl.execute("test");
  Assertions.assertEquals("SUCCESS", res);
 }

 @Test
 public void testExecuteInvalid() {
  List<String> status = Arrays.asList("Valid", "InValid");
  when(connectReplicationStatusRepo.fetchStatusFromConnectReplicationStatus()).thenReturn(status);
  String res = runDataRefreshServiceImpl.execute("test");
  Assertions.assertEquals("SUCCESS", res);
 }

 @Test
 public void testExecuteInvalidEmpty() {
  List<String> status = Arrays.asList();
  when(connectReplicationStatusRepo.fetchStatusFromConnectReplicationStatus()).thenReturn(status);
  String res = runDataRefreshServiceImpl.execute("test");
  Assertions.assertEquals("SUCCESS", res);
 }

 @Test
 public void testExecutedataRefreshLogEntityFailed() {
  List<String> status = Arrays.asList("Valid", "Valid");
  DataRefreshLogEntity dataRefreshLogEntity = DataRefreshLogEntity.builder().status("Failed").build();
  when(connectReplicationStatusRepo.fetchStatusFromConnectReplicationStatus()).thenReturn(status);
  when(dataRefreshLogRepo.fetchDataRefreshLog()).thenReturn(dataRefreshLogEntity);
  EbillRunDataRefreshResponse response = EbillRunDataRefreshResponse.builder().message("Success").build();
  ResponseEntity<EbillRunDataRefreshResponse> responseEntity = ResponseEntity.status(
      HttpStatus.OK).body(response);
  when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
      eq(EbillRunDataRefreshResponse.class))).thenReturn(responseEntity);
  Assertions.assertThrows(RuntimeException.class, () -> runDataRefreshServiceImpl.execute("test"));
 }

 @Test
 public void testExecuteSuccess() {
  List<String> status = Arrays.asList("Valid", "Valid");
  DataRefreshLogEntity dataRefreshLogEntity = DataRefreshLogEntity.builder().status("Completed").build();
  when(dataRefreshLogRepo.fetchDataRefreshLog()).thenReturn(dataRefreshLogEntity);
  when(connectReplicationStatusRepo.fetchStatusFromConnectReplicationStatus()).thenReturn(status);
  EbillRunDataRefreshResponse response = EbillRunDataRefreshResponse.builder().message("Success").build();
  ResponseEntity<EbillRunDataRefreshResponse> responseEntity = ResponseEntity.status(
      HttpStatus.OK).body(response);
  when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
      eq(EbillRunDataRefreshResponse.class))).thenReturn(responseEntity);
  String res = runDataRefreshServiceImpl.execute("test");
  Assertions.assertEquals("SUCCESS", res);
 }
}