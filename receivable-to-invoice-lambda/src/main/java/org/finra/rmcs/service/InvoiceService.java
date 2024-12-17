package org.finra.rmcs.service;

public interface InvoiceService {
  void convertReceivableToInvoice(String correlationId);
}
