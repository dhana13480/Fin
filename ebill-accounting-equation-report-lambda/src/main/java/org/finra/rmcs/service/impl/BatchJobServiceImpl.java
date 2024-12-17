package org.finra.rmcs.service.impl;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.entity.BatchJobEntity;
import org.finra.rmcs.repo.BatchJobRepo;
import org.finra.rmcs.service.BatchJobService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BatchJobServiceImpl implements BatchJobService {

  BatchJobRepo batchJobRepo;

  public BatchJobServiceImpl(BatchJobRepo batchJobRepo) {
    this.batchJobRepo = batchJobRepo;
  }

  @Override
  public BatchJobEntity save(UUID id, String jobName, String jobStatus) {
    BatchJobEntity entity = BatchJobEntity.builder().id(id).name(jobName).status(jobStatus).build();
    return batchJobRepo.saveAndFlush(entity);
  }

  @Override
  public void update(BatchJobEntity entity) {
    batchJobRepo.save(entity);
  }
  @Override
  public BatchJobEntity findByName( String jobName) {
    return batchJobRepo.findByName(jobName);
  }

}
