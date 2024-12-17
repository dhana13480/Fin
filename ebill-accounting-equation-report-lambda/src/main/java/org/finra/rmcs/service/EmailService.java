package org.finra.rmcs.service;

public interface EmailService {

  boolean sendEMail(
      String fromEmailAddress,
      String toEmailAddress,
      String ccEmailAddress,
      String subject,
      String emailContent);

  boolean sendAttachmentEMail(
          String fromEmailAddress,
          String toEmailAddress,
          String ccEmailAddress,
          String subject,
          String emailContent,
          byte[] workBookBytes,
          String fileName,boolean isAccEquationReport);
}
