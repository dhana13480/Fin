package org.finra.rmcs.service.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.repo.ReceivableRevenueStreamRepo;
import org.finra.rmcs.service.ReceivableRevenueStreamService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ReceivableRevenueStreamServiceImpl implements ReceivableRevenueStreamService {

  private final ReceivableRevenueStreamRepo receivableRevenueStreamRepo;

  public List<String> isSendToWDEnabled() {
    return receivableRevenueStreamRepo.findByRevenueStreamNameAndSendToWD();
  }
}
