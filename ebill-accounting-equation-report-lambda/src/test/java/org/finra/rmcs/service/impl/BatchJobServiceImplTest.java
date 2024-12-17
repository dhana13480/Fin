package org.finra.rmcs.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.UUID;
import org.finra.rmcs.entity.BatchJobEntity;
import org.finra.rmcs.repo.BatchJobRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class BatchJobServiceImplTest {

  @InjectMocks BatchJobServiceImpl batchJobService;
  @Mock BatchJobRepo batchJobRepo;
  BatchJobEntity batchJobEntity;
  UUID correlationId;
  @BeforeEach
  void setUp() {
     correlationId =UUID.randomUUID();

    batchJobEntity=BatchJobEntity.builder()
        .id(correlationId)
        .status("Status")
        .build();
  }

  @Test
  public void testSave() throws Exception {
    when(batchJobRepo.saveAndFlush(any())).thenReturn(batchJobEntity);
    BatchJobEntity entity= batchJobService.save(UUID.randomUUID(),"ACCEQUATIONREPORT","STARTED");
    Assertions.assertEquals(entity.getId(),correlationId);
  }
  @Test
  public void testUpdate() throws Exception {
    BatchJobEntity savedEntity= BatchJobEntity.builder()
        .id(correlationId)
        .build();
    batchJobService.update(savedEntity);
  }

}
