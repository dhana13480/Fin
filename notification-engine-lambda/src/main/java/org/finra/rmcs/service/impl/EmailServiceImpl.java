package org.finra.rmcs.service.impl;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.AlertEventStatusEnum;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.constants.EventTypeEnum;
import org.finra.rmcs.constants.ModuleTypeEnum;
import org.finra.rmcs.dto.*;
import org.finra.rmcs.entity.AlertEmailEventEntity;
import org.finra.rmcs.entity.EmailConfig;
import org.finra.rmcs.entity.EwsUserDetailViewEntity;
import org.finra.rmcs.entity.PaymentTrackingEntity;
import org.finra.rmcs.entity.SubscriptionEntity;
import org.finra.rmcs.model.NotificationMessage;
import org.finra.rmcs.repo.*;
import org.finra.rmcs.service.EmailService;
import org.finra.rmcs.service.EmailTrackingService;
import org.finra.rmcs.service.EwsSerrvice;
import org.finra.rmcs.service.OrganizationService;
import org.finra.rmcs.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@Slf4j
@Component
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EmailConfigRepo emailConfigRepo;
    @Autowired
    private PaymentTrackingRepo paymentTrackingRepo;
    @Autowired
    private AlertEmailEventRepo alertEmailEventRepo;
    @Autowired
    private EwsUserView ewsUserView;
    @Autowired
    private EwsSerrvice ewsSerrvice;
    @Autowired
    private Util utils;

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private EmailTrackingService emailTrackingService;

    @Autowired
    private GatewayEmailNotificationServiceImpl gatewayEmailNotificationService;
    @Autowired
    private SubscriptionRepo subscriptionRepo;

    @Autowired
    private RevenueStreamAppConfigRepo revenueStreamAppConfigRepo;


    @Override
    public void sendEmailNotification(NotificationMessage notificationMessage) {
        log.info("notificationMessage{} ", notificationMessage);
        GatewayEmailNotificationRequest request = null;
        EmailResponse response = new EmailResponse();
        GatewayEmailNotificationResponse[] gatewayEmailNotificationResponseList;
        EmailConfig emailConfig = emailConfigRepo.findByEventTypeName(EventTypeEnum.findbyPaymentTypeAndEventName
                (notificationMessage.getModule(), notificationMessage.getStatus()));
        log.info("emailConfig {}", emailConfig);
        if(Constants.ENABLER.equalsIgnoreCase(notificationMessage.getModule())){
            request = setNotificationRequest(emailConfig,null, null, null,null,notificationMessage, null);
            gatewayEmailNotificationResponseList = gatewayEmailNotificationService.sendGatewayEmailNotification(request,emailConfig);
            if (gatewayEmailNotificationResponseList != null && gatewayEmailNotificationResponseList.length > 0) {
                response.setGatewayEmailNotificationResponseList(gatewayEmailNotificationResponseList);
            }
        }
        else {
            EmailConfig emailConfigForInvoiceAlerts = null;
            if (notificationMessage.getModule() == null || notificationMessage.getModule().isEmpty()) {
                emailConfigForInvoiceAlerts = emailConfigRepo.findByEventTypeName(notificationMessage.getStatus());
                log.info("emailConfigForInvoiceAlerts {}", emailConfigForInvoiceAlerts);
            }
            List<PaymentTrackingEntity> paymentTrackingEntity = paymentTrackingRepo.findByPaymentReferenceNumberIn
                    (notificationMessage.getPaymentNumber());
            log.info("paymentTrackingEntity {} ", paymentTrackingEntity);
            List<AlertEmailEventEntity> alertEmailEventEntity = alertEmailEventRepo.findAllById(Arrays.asList(notificationMessage.getAlertEventEmailId()));
            log.info("alertEmailEventEntity {} ", alertEmailEventEntity);
            List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = alertEmailEventRepo.findByInvoiceId
                    (notificationMessage.getInvoiceNumber(),notificationMessage.getStatus());
            log.info("alertEmailEventEntityForInvoiceAlerts {} ", alertEmailEventEntityForInvoiceAlerts);


            if ((emailConfig != null && orgService != null && (!paymentTrackingEntity.isEmpty() || StringUtils.equals(ModuleTypeEnum.AUTOPAY.name(), notificationMessage.getModule())))
                    || StringUtils.equals(ModuleTypeEnum.INVOICE_AUTOPAY.name(), notificationMessage.getModule())) {
                Map<String, String> orgMap;
                orgMap = orgService.getOrganizationDetailsByBUAndPaymentNumber(Constants.CRDRG, paymentTrackingEntity);
                request = setNotificationRequest(emailConfig, emailConfigForInvoiceAlerts, paymentTrackingEntity, alertEmailEventEntity, alertEmailEventEntityForInvoiceAlerts, notificationMessage, orgMap);
                gatewayEmailNotificationResponseList = gatewayEmailNotificationService.sendGatewayEmailNotification(request,emailConfig);
                if (gatewayEmailNotificationResponseList != null && gatewayEmailNotificationResponseList.length > 0) {
                    response.setGatewayEmailNotificationResponseList(gatewayEmailNotificationResponseList);
                }
            } else if ((emailConfig != null && !alertEmailEventEntity.isEmpty()) || (emailConfigForInvoiceAlerts != null)) {
                Map<String, String> orgMap = null;

                request = setNotificationRequest(emailConfig, emailConfigForInvoiceAlerts, paymentTrackingEntity, alertEmailEventEntity, alertEmailEventEntityForInvoiceAlerts, notificationMessage, orgMap);
               if(emailConfig == null){
                   emailConfig = emailConfigForInvoiceAlerts;
               }
                gatewayEmailNotificationResponseList = gatewayEmailNotificationService.sendGatewayEmailNotification(request,emailConfig);
                log.info("gatewayEmailNotificationResponseList ", gatewayEmailNotificationResponseList);
                if (gatewayEmailNotificationResponseList == null) {
                    if (alertEmailEventEntity != null && !alertEmailEventEntity.isEmpty()) {
                        alertEmailEventEntity.get(0).setStatus(AlertEventStatusEnum.ERROR.getValue());
                        alertEmailEventEntity.get(0).setSendTS(LocalDate.now());
                        alertEmailEventRepo.saveAll(alertEmailEventEntity);
                    } else {
                        List<AlertEmailEventEntity> updatedStatus;
                        updatedStatus = alertEmailEventEntityForInvoiceAlerts.stream().map(updatedalertEmailEventEntity -> {

                            updatedalertEmailEventEntity.setStatus(AlertEventStatusEnum.ERROR.getValue());
                            updatedalertEmailEventEntity.setSendTS(LocalDate.now());
                            return updatedalertEmailEventEntity;

                        }).collect(Collectors.toList());
                        log.info("alert", updatedStatus);

                        alertEmailEventRepo.saveAll(updatedStatus);
                    }
                }
                if (gatewayEmailNotificationResponseList != null && gatewayEmailNotificationResponseList.length > 0) {
                    if (alertEmailEventEntity != null && !alertEmailEventEntity.isEmpty()) {
                        alertEmailEventEntity.get(0).setStatus(AlertEventStatusEnum.PROCESSED.getValue());
                        alertEmailEventEntity.get(0).setSendTS(LocalDate.now());
                        alertEmailEventRepo.saveAll(alertEmailEventEntity);
                    } else {
                        List<AlertEmailEventEntity> updatedStatus ;
                        updatedStatus = alertEmailEventEntityForInvoiceAlerts.stream().map(updatedalertEmailEventEntity -> {
                            updatedalertEmailEventEntity.setStatus(AlertEventStatusEnum.PROCESSED.getValue());
                            updatedalertEmailEventEntity.setSendTS(LocalDate.now());
                            return updatedalertEmailEventEntity;


                        }).collect(Collectors.toList());
                        alertEmailEventRepo.saveAll(updatedStatus);
                    }
                    response.setGatewayEmailNotificationResponseList(gatewayEmailNotificationResponseList);
                }
            }
        }


        // save tracking records
        log.info("calling email tracking service to save records");
        emailTrackingService.saveEmailTrackingDetails(notificationMessage, request, response);
        log.info("end sendEmailNotification");
    }


    public GatewayEmailNotificationRequest setNotificationRequest(EmailConfig emailConfig,EmailConfig emailConfigForInvoiceAlerts, List<PaymentTrackingEntity> paymentTrackingEntity,List<AlertEmailEventEntity> alertEmailEventEntity, List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts , NotificationMessage notificationMessage, Map<String, String> orgMap)  {
        log.info("ewsUser {}", notificationMessage.getEwsUser());
        String invoiceTypeDescription = revenueStreamAppConfigRepo.findbyRevenueStreamNameDesc(notificationMessage.getInvoiceType());
        log.info("invoiceTypeDescription {}", invoiceTypeDescription);
        GatewayEmailNotificationRequest request = new GatewayEmailNotificationRequest();
        if (Constants.ENABLER.equalsIgnoreCase(notificationMessage.getModule())) {
            if (StringUtils.equalsAnyIgnoreCase(emailConfig.getEventTypeName(),
                    EventTypeEnum.ENABLER_RUN_DATA_REFRESH_SUCCESS.name(), EventTypeEnum.ENABLER_RUN_DATA_REFRESH_TIME_FOUR_THIRTY_EXCEED.name(),
                    EventTypeEnum.ENABLER_RUN_DATA_REFRESH_TIME_SEVEN_EXCEED.name(),EventTypeEnum.ENABLER_DATA_REFRESH_LOG_SUCCESS.name())) {
                request.setTo(Util.parseSemiColonConcatenatedEmails(emailConfig.getTo()));
                request.setEventName(emailConfig.getEventTypeName());
                request.setBody(emailConfig.getBody());
                request.setSubject(emailConfig.getSubject());
                request.setFeedback(emailConfig.getFeedBack());
            }
        }
        else if (ewsSerrvice != null) {
            log.info("ewsUser {}", notificationMessage.getEwsUser());
            ResponseEntity<EwsAccountInformationResponse> response = ewsSerrvice.getAccountInformation(notificationMessage.getEwsUser());
            if((emailConfig != null && (paymentTrackingEntity != null || StringUtils.equals(notificationMessage.getModule(), ModuleTypeEnum.AUTOPAY.name())))
                ||(emailConfigForInvoiceAlerts != null) || StringUtils.equals(notificationMessage.getModule(), ModuleTypeEnum.INVOICE_AUTOPAY.name())
                || StringUtils.equals(notificationMessage.getModule(), ModuleTypeEnum.REALLOCATION_INVOICE.name())) {
                request.setTo(List.of(response.getBody().getPersonalInfo().getEmail()));

                if ((StringUtils.equals(notificationMessage.getModule(), ModuleTypeEnum.AUTOPAY.name())) || (StringUtils.equals(notificationMessage.getModule(), ModuleTypeEnum.INVOICE_AUTOPAY.name()))) {
                    Optional<SubscriptionEntity> subscriptionEntity = subscriptionRepo.findById(UUID.fromString(notificationMessage.getSubscriptionId()));
                    if(subscriptionEntity.isPresent()) {
                        request.setBody(Util.generateAutoPayBody(emailConfig != null ?emailConfig.getBody() : null, paymentTrackingEntity, notificationMessage, subscriptionEntity.get(),invoiceTypeDescription));
                        if (!StringUtils.isBlank(emailConfig != null ? emailConfig.getCc(): null)) {
                            EwsUserDetailViewEntity user = ewsUserView.findByOrgId(Integer.parseInt(subscriptionEntity.get().getOrgId()));
                            if(user != null && !user.getUserId().equals(notificationMessage.getEwsUser())) {
                                request.setCc(List.of(user.getEmailId()));
                            }
                        }
                        request.setSubject(Util.generateSubject(emailConfig.getSubject(), subscriptionEntity.get().getOrgId(),notificationMessage.getInvoiceType()));
                    }
                } else if (StringUtils.equalsIgnoreCase(notificationMessage.getModule(), Constants.MODULE_REFUND)) {
                    request.setBody(Util.generateRefundBody(emailConfig != null ?emailConfig.getBody() : null, notificationMessage, response,paymentTrackingEntity, orgMap));
                }else {
                    if(emailConfig == null){
                        emailConfig = emailConfigForInvoiceAlerts;
                    }
                    request.setBody(Util.generateBody(emailConfig != null ?emailConfig.getBody() : null,paymentTrackingEntity, alertEmailEventEntity, emailConfig,orgMap,notificationMessage,alertEmailEventEntityForInvoiceAlerts,invoiceTypeDescription));
                }
                request.setEventName(emailConfig != null ? emailConfig.getEventTypeName() : null);
                request.setFeedback(emailConfig != null ? emailConfig.getFeedBack() : null);
                if(StringUtils.isBlank(request.getSubject())) {
                    if ((StringUtils.equals(notificationMessage.getModule(), Constants.CRDRG))||StringUtils.equalsAnyIgnoreCase(notificationMessage.getStatus(),EventTypeEnum.NEW_INVOICE_AVAILABLE.name(),EventTypeEnum.INVOICE_PAST_DUE.name())){
                        request.setSubject(Util.generateSubject(emailConfig.getSubject(), alertEmailEventEntity,notificationMessage, alertEmailEventEntityForInvoiceAlerts));
                    } else if(StringUtils.equalsIgnoreCase(notificationMessage.getModule(), Constants.MODULE_REFUND)){
                        request.setSubject(Util.generateSubject(emailConfig.getSubject(), alertEmailEventEntity,notificationMessage, alertEmailEventEntityForInvoiceAlerts));

                    }
                    else {
                        request.setSubject(emailConfig.getSubject());
                    }
                }
            }
        }
        return request;
    }
}





