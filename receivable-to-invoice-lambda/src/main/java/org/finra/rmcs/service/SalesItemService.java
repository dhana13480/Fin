package org.finra.rmcs.service;

import java.util.List;
import org.finra.rmcs.entity.SalesItemEntity;

public interface SalesItemService {
  List<SalesItemEntity> getSalesItemList(List<String> revenueStreams);
}
