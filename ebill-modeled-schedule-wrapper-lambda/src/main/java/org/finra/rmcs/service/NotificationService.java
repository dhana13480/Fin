package org.finra.rmcs.service;

import org.hibernate.service.spi.ServiceException;


public interface NotificationService {

  void publishNotificationEvent(String correlationId, String module, String status) throws ServiceException;

}
