package org.finra.rmcs.service.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.entity.SalesItemEntity;
import org.finra.rmcs.repo.SalesItemRepo;
import org.finra.rmcs.service.SalesItemService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SalesItemServiceImpl implements SalesItemService {

  private final SalesItemRepo salesItemRepo;

  /** Get sales item list */
  public List<SalesItemEntity> getSalesItemList(List<String> revenueStreams) {
    return salesItemRepo.findByRevenueStreams(revenueStreams);
  }
}
