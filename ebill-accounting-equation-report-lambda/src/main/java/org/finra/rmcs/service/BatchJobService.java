package org.finra.rmcs.service;


import java.util.UUID;
import org.finra.rmcs.entity.BatchJobEntity;

public interface BatchJobService {
  public BatchJobEntity save(UUID id, String jobName, String jobStatus);
  public void update(BatchJobEntity entity);
  public BatchJobEntity findByName(String  jobName);
}
