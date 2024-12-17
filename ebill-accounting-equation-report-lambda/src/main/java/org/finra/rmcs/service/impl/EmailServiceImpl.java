package org.finra.rmcs.service.impl;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service("emailService")
@Slf4j
public class EmailServiceImpl implements EmailService {
  private static final String EMAIL_ATTACHMENT_TYPE ="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  private final JavaMailSender javaMailSender;

  @Autowired
  public EmailServiceImpl(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

  @Override
  public boolean sendEMail(
      String fromEmailAddress,
      String toEmailAddress,
      String ccEmailAddress,
      String subject,
      String emailContent)
      throws MailException {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(fromEmailAddress);
    message.setTo(toEmailAddress);
    message.setCc(ccEmailAddress);
    message.setSubject(subject);
    message.setText(emailContent);

    javaMailSender.send(message);
    log.info("Email subject: {}", subject);
    log.info("Email content: {}", emailContent);
    log.info("Sent email from {} to --> {}", fromEmailAddress, toEmailAddress);

    return true;
  }

  @Override
  public boolean sendAttachmentEMail(String fromEmailAddress, String toEmailAddress, String ccEmailAddress, String subject,
                                     String emailContent, byte[] workBookBytes,String fileName,boolean isAccEquationReport) {
    MimeMessagePreparator preparator =
        new MimeMessagePreparator() {
          @Override
          public void prepare(MimeMessage message) throws Exception {
            message.setFrom(new InternetAddress(fromEmailAddress));
            String[] toEmails = toEmailAddress.split(",");
            InternetAddress[] toRecipientAddress = new InternetAddress[toEmails.length];
            for (int i = 0; i < toEmails.length; i++) {
              toRecipientAddress[i] = new InternetAddress(toEmails[i]);
            }
            if (!StringUtils.isBlank(ccEmailAddress)) {
              String[] ccEmails = ccEmailAddress.split(",");
              InternetAddress[] ccRecipientAddress = new InternetAddress[ccEmails.length];
              for (int i = 0; i < ccEmails.length; i++) {
                ccRecipientAddress[i] = new InternetAddress(ccEmails[i]);
              }
              message.setRecipients(Message.RecipientType.CC, ccRecipientAddress);
            }
            message.setRecipients(Message.RecipientType.TO, toRecipientAddress);
            message.setSubject(subject);
            File outputFile =
                new File(Constants.TEMP_FOLDER + fileName + Constants.CSV_FILE_EXTENSION);
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
              outputStream.write(workBookBytes);
            }
            FileSystemResource file = new FileSystemResource(outputFile);
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            if (isAccEquationReport) {
              helper.addAttachment(
                  fileName + Constants.XLSX_FILE_EXTENSION, file, EMAIL_ATTACHMENT_TYPE);
            } else {
              helper.addAttachment(
                  fileName + Constants.CSV_FILE_EXTENSION, file, EMAIL_ATTACHMENT_TYPE);
            }
            helper.setText(emailContent, true);
          }
        };
    try{
      javaMailSender.send(preparator);
    }catch(Exception ex){
      log.error("Exception while sending email:{}", ex);
    }

    return true;
  }

private static class ByteArrayDataSource implements DataSource{
private final  byte[] data;
private final String type;

  public ByteArrayDataSource(byte[] data, String type) {
    this.data = data;
    this.type = type;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(data);
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    throw new UnsupportedOperationException("Not Supported");
  }

  @Override
  public String getContentType() {
    return type;
  }

  @Override
  public String getName() {
    return "ByteArrayDataSource";
  }
}

}
