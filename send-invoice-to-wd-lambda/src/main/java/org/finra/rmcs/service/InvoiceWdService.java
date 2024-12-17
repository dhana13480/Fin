package org.finra.rmcs.service;

import java.util.Map;

public interface InvoiceWdService {

  void sentReceivableInvoiceToWd(String correlationId, String revenueStream);

  String testOperationsForExternalBucket(Map<String, Object> input);
}
