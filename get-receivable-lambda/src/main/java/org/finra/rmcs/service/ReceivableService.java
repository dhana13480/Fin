package org.finra.rmcs.service;

import java.util.List;
import org.finra.rmcs.entity.ReceivableEntity;

public interface ReceivableService {

  List<ReceivableEntity> findValidReceivablesByInvoiceIds(List<String> invoiceIds);
}
