package org.finra.rmcs.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.util.List;
import org.finra.rmcs.repo.SalesItemRepo;
import org.finra.rmcs.service.impl.SalesItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class SalesItemServiceTest {

  @Mock
  private SalesItemRepo salesItemRepo;

  @InjectMocks
  private SalesItemServiceImpl salesItemService;

  @Test
  public void getSalesItemListTest() {
    when(salesItemRepo.findByRevenueStreams(anyList())).thenReturn(null);
    List<String> revenueStreams = List.of("test");
    assertNull(salesItemService.getSalesItemList(revenueStreams));
  }
}
