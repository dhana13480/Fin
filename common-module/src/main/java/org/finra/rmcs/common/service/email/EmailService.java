package org.finra.rmcs.common.service.email;

import jakarta.mail.MessagingException;

public interface EmailService {

  boolean sendEMail(
      String fromEmailAddress,
      String toEmailAddress,
      String ccEmailAddress,
      String subject,
      String emailContent)
      throws MessagingException;

  boolean sendAttachmentEMail(
          String fromEmailAddress,
          String toEmailAddress,
          String ccEmailAddress,
          String subject,
          String emailContent,
          byte[] workBookBytes,
          String fileName)
          throws MessagingException;
}
