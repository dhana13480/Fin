package org.finra.rmcs.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.constants.EventTypeEnum;
import org.finra.rmcs.dto.EmailResponse;
import org.finra.rmcs.dto.GatewayEmailNotificationRequest;
import org.finra.rmcs.entity.EmailTracking;
import org.finra.rmcs.entity.PaymentEmailTracking;
import org.finra.rmcs.model.NotificationMessage;
import org.finra.rmcs.repo.EmailTrackingRepo;
import org.finra.rmcs.repo.PaymentEmailTrackingRepo;
import org.finra.rmcs.service.EmailTrackingService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class EmailTrackingServiceImpl implements EmailTrackingService {

  EmailTrackingRepo emailTrackingRepo;
  PaymentEmailTrackingRepo paymentEmailTrackingRepo;

  ObjectMapper objectMapper;
  @Transactional
  @Override
  public void saveEmailTrackingDetails(NotificationMessage notificationMessage,
    GatewayEmailNotificationRequest request, EmailResponse response) {
    log.info("Start saveEmailTrackingDetails");
    try{
      if(response.getGatewayEmailNotificationResponseList()!=null && response.getGatewayEmailNotificationResponseList().length>0){
        // save record in email tracking
        String trackingId = response.getGatewayEmailNotificationResponseList()[0].getId();
        log.info("gateway email tracking id:{}", trackingId);
        EmailTracking emailTracking = EmailTracking.builder().trackingId(trackingId)
            .eventTypeName(EventTypeEnum.findbyPaymentTypeAndEventName(notificationMessage.getModule(), notificationMessage.getStatus()))
            .requestPayload(objectMapper.writeValueAsString(request))
            .responsePayload(objectMapper.writeValueAsString(response))
            .createdBy(Constants.NOTIFICATION_ENGINE)
            .updatedBy(Constants.NOTIFICATION_ENGINE).build();
        EmailTracking savedEmailTracking = emailTrackingRepo.save(emailTracking);
        //save record in payment email tracking
        if (notificationMessage.getPaymentNumber() != null) {
          List<PaymentEmailTracking> paymentEmailTrackingList = new ArrayList<>();
          for (String paymentNumber : notificationMessage.getPaymentNumber()) {
            PaymentEmailTracking paymentEmailTracking = PaymentEmailTracking.builder().paymentReferenceNumber(paymentNumber)
                    .trackingId(savedEmailTracking.getId().toString()).createdBy(Constants.NOTIFICATION_ENGINE)
                    .updatedBy(Constants.NOTIFICATION_ENGINE).build();
            paymentEmailTrackingList.add(paymentEmailTracking);
          }

          if (!paymentEmailTrackingList.isEmpty()) {
            paymentEmailTrackingRepo.saveAll(paymentEmailTrackingList);
          }
        }
      }
    }catch (Exception ex){
      log.error("Exception while saving the email tracking/payment email tracking records, ex:{}", ex);
      throw new RuntimeException(ex);
    }

    log.info("end saveEmailTrackingDetails");
  }
}
