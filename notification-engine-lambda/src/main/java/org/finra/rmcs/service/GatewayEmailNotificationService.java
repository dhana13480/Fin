package org.finra.rmcs.service;

import org.finra.rmcs.dto.GatewayEmailNotificationRequest;
import org.finra.rmcs.dto.GatewayEmailNotificationResponse;
import org.finra.rmcs.entity.EmailConfig;

public interface GatewayEmailNotificationService {
    GatewayEmailNotificationResponse[] sendGatewayEmailNotification(
            GatewayEmailNotificationRequest request,EmailConfig emailConfig);

}