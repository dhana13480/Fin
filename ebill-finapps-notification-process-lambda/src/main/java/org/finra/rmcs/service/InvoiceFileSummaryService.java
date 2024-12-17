package org.finra.rmcs.service;

import org.finra.rmcs.dto.DmNotification;

public interface InvoiceFileSummaryService {
  public void storeDmNotificationIntoInvoiceFileSummary(String correlationId, DmNotification notificationMessage);
}
