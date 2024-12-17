package org.finra.rmcs.service;

import org.finra.rmcs.model.NotificationMessage;


public interface EmailService {
    void  sendEmailNotification(NotificationMessage notificationMessage);

}