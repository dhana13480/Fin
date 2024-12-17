package org.finra.rmcs.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.dto.GatewayEmailNotificationRequest;
import org.finra.rmcs.dto.Notifications;
import org.finra.rmcs.entity.EmailConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class GatewayEmailNotificationUtilTest {

  public static GatewayEmailNotificationRequest gatewayEmailNotificationRequest;
  public static GatewayEmailNotificationRequest gatewayEmailNotificationFeedBackNullRequest;
  String nonProdEmail;

  @BeforeEach
  public void before() {
    gatewayEmailNotificationRequest = new GatewayEmailNotificationRequest();
    gatewayEmailNotificationRequest.setEventName("Email Test");
    gatewayEmailNotificationRequest.setTo(new ArrayList<String>(Arrays.asList("testTo@finra.org")));
    gatewayEmailNotificationRequest.setCc(new ArrayList<String>(Arrays.asList("testCc@finra.org")));
    gatewayEmailNotificationRequest.setBcc(
        new ArrayList<String>(Arrays.asList("testBcc@finra.org")));
    gatewayEmailNotificationRequest.setBody("Email test body");
    gatewayEmailNotificationRequest.setFeedback("Email test feedback");
    gatewayEmailNotificationRequest.setSubject("Email test subject");

    gatewayEmailNotificationFeedBackNullRequest = new GatewayEmailNotificationRequest();
    gatewayEmailNotificationFeedBackNullRequest.setEventName("Email Test");
    gatewayEmailNotificationFeedBackNullRequest.setTo(
        new ArrayList<String>(Arrays.asList("testTo@finra.org")));
    gatewayEmailNotificationFeedBackNullRequest.setCc(
        new ArrayList<String>(Arrays.asList("testCc@finra.org")));
    gatewayEmailNotificationFeedBackNullRequest.setBcc(
        new ArrayList<String>(Arrays.asList("testBcc@finra.org")));
    gatewayEmailNotificationFeedBackNullRequest.setBody("Email test body");
    gatewayEmailNotificationFeedBackNullRequest.setSubject("Email test subject");
  }

  @Test
  public void generateNotificationsRequestTest() {
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setTo("abc@finra.org");
    Notifications[] notifications = GatewayEmailNotificationUtil.generateNotificationsRequest(
        gatewayEmailNotificationRequest, emailConfig, nonProdEmail);
    Assertions.assertEquals(Constants.TEMPLATE_NAME, notifications[0].getTemplateName());
  }

  @Test
  public void generateNotificationsRequestWithFeedBackNullTest() {
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setTo("abc@finra.org");
    Notifications[] notifications = GatewayEmailNotificationUtil.generateNotificationsRequest(
        gatewayEmailNotificationFeedBackNullRequest, emailConfig, nonProdEmail);
    Assertions.assertEquals(Constants.TEMPLATE_NAME, notifications[0].getTemplateName());
  }

  @Test
  public void generateNotificationsRequestExceptionTest()
      throws NoSuchMethodException {
    Constructor<GatewayEmailNotificationUtil> pcc = GatewayEmailNotificationUtil.class.getDeclaredConstructor();
    pcc.setAccessible(true);

    Throwable currentException = null;
    try {
      pcc.newInstance();
    } catch (Exception exception) {
      currentException = exception;
    }
    Assertions.assertTrue(currentException instanceof InvocationTargetException);
  }
}
