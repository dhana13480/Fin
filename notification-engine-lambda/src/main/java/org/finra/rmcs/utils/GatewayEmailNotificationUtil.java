package org.finra.rmcs.utils;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.dto.ApplicationDelivery;
import org.finra.rmcs.dto.Audience;
import org.finra.rmcs.dto.AudienceDelivery;
import org.finra.rmcs.dto.DefaultTemplateData;
import org.finra.rmcs.dto.EmailSecondaryRecipient;
import org.finra.rmcs.dto.GatewayEmailNotificationRequest;
import org.finra.rmcs.dto.Notifications;
import org.finra.rmcs.dto.TemplateData;
import org.finra.rmcs.dto.TemplateReplacementData;
import org.finra.rmcs.entity.EmailConfig;

@Slf4j
public class GatewayEmailNotificationUtil {

  private GatewayEmailNotificationUtil()  throws InvocationTargetException {
    throw new InvocationTargetException(null);
  }

  public static Notifications[] generateNotificationsRequest(GatewayEmailNotificationRequest request, EmailConfig emailConfig,String nonProdEmail){
    log.info("start generateNotificationsRequest");
    List<String> emails = new ArrayList<>();
    String toAddress = null;
    String ccAddress = null;
    String bccAddress = null;
    List<String> ccBccEmails = new ArrayList<>();
    if (!"PROD".equalsIgnoreCase(System.getenv(Constants.SPRING_PROFILES_ACTIVE))) {
      if(emailConfig.getTo().equalsIgnoreCase(Constants.BUYER_EMAIL)){
        toAddress = nonProdEmail + Constants.CONST_SEMI_COLON + request.getTo().get(0);
      } else {
        toAddress = nonProdEmail;
      }
       if(request.getCc()!= null) {
            ccAddress = Constants.NON_PROD_CC_TEST_EMAIL+ Constants.CONST_SEMI_COLON + request.getCc().get(0);
       }
       else{
           ccAddress= Constants.NON_PROD_CC_TEST_EMAIL;
       }

      bccAddress = Constants.NON_PROD_BCC_TEST_EMAIL;
      emails.addAll(parseSemiColonConcatenatedEmails(toAddress));
    }
    else {
      toAddress = emailConfig.getTo();
      ccAddress = emailConfig.getCc();
      bccAddress = emailConfig.getBcc();
        if(emailConfig.getTo().equalsIgnoreCase(Constants.BUYER_EMAIL)){
            emails.addAll(request.getTo());

        } else {
            emails.addAll(parseSemiColonConcatenatedEmails(toAddress));
        }
    }

    ccBccEmails.addAll(parseSemiColonConcatenatedEmails(ccAddress));
    ccBccEmails.addAll(parseSemiColonConcatenatedEmails(bccAddress));

    Notifications[] notificationsArr = new Notifications[1];
    TemplateData templateData = new TemplateData();
    templateData.setName(request.getEventName());
    templateData.setU4RequestGroup("");
    TemplateReplacementData templateReplacementData = new TemplateReplacementData();
    AudienceDelivery audienceDelivery = new AudienceDelivery();
    Audience[] audience = new Audience[1];
    audience[0] = new Audience();
    audience[0].setAudienceDetails(emails.toArray(new String[0]));
    audience[0].setAudienceType(Constants.AUDIENCE_TYPE);
    audienceDelivery.setAudience(audience);
    ApplicationDelivery applicationDelivery = new ApplicationDelivery();
    applicationDelivery.setAudienceDelivery(audienceDelivery);
    notificationsArr[0] = new Notifications();
    Notifications notifications = notificationsArr[0];
    notifications.setApplicationDelivery(applicationDelivery);
    DefaultTemplateData defaultTemplateData = new DefaultTemplateData();
    defaultTemplateData.setBody(request.getBody());
    defaultTemplateData.setFeedback(request.getFeedback()!=null ? request.getFeedback() : " ");
    defaultTemplateData.setActionLink(Constants.ACTION_LINK);
    defaultTemplateData.setSupportName(Constants.FINRA_HELP_DESK);
    defaultTemplateData.setSupportContact(Constants.SUPPORT_CONTACT);
    defaultTemplateData.setTitle(request.getSubject());
    defaultTemplateData.setSupportDayEnd(Constants.SUPP_END_DAY);
    defaultTemplateData.setSupportDayStart(Constants.SUPP_START_DAY);
    defaultTemplateData.setSupportTimeStart(Constants.SUPP_START_TIME);
    defaultTemplateData.setSupportTimeEnd(Constants.SUPP_END_TIME);
    defaultTemplateData.setU4RequestGroup(Constants.U4_REQ_GRP);
    templateReplacementData.setDefaultTemplateData(defaultTemplateData);
    templateReplacementData.setReplacements(new ArrayList<>());
    notifications.setDeliverEmail(true);
    notifications.setTemplateName(Constants.TEMPLATE_NAME);
    notifications.setTemplateVersion(1);
    notifications.setSubscriptionGroupName(Constants.SUBSCRIPTION_GRP_NAME);
    notifications.setSubscriptionTypeName(Constants.SUBSCRIPTION_TYPE_NAME);
    notifications.setSourceApplication(Constants.SOURCE_APP_NAME);
    notifications.setActiveDate(Constants.NOW);
    notifications.setExpirationDate(LocalDateTime.now().plusDays(1).toString());
    notifications.setType(Constants.ANNOUNCEMENT);
    notifications.setActionable(false);
    notifications.setUrgencyIndicator(false);
    notifications.setPublishedStatus(Constants.PUBLISED);
    notifications.setTemplateReplacementData(templateReplacementData);
    EmailSecondaryRecipient emailSecondaryRecipient = new EmailSecondaryRecipient();
    emailSecondaryRecipient.setBccRecipient(ccBccEmails.toArray(new String[0]));
    notifications.setEmailSecondaryRecipient(emailSecondaryRecipient);
    log.info("end generateNotificationsRequest");
    return notificationsArr;
  }
  private static List<String> parseSemiColonConcatenatedEmails(String emailString) {
    List<String> emails = new ArrayList<>();
    // Empty
    if(StringUtils.isBlank(emailString)) {
      return emails;
    }

    // Single email
    if(emailString.indexOf(Constants.CONST_SEMI_COLON) < 0) {
      emails.add(emailString);
    }else {
      // multi emails separated by ";" or one email with ending ";"
      Arrays.stream(emailString.split(Constants.CONST_SEMI_COLON)).forEach(s -> emails.add(s));
    }
    return emails;
  }

}
