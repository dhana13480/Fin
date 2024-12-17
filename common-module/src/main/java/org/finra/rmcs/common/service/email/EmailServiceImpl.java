package org.finra.rmcs.common.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
                                     String emailContent, byte[] workBookBytes,String fileName) throws MessagingException {
    MimeMessage message = javaMailSender.createMimeMessage();
    message.setFrom(new InternetAddress(fromEmailAddress));
    String [] toEmails = toEmailAddress.split(",");
    InternetAddress[] toRecipientAddress = new InternetAddress[toEmails.length];
    for(int i=0;i<toEmails.length;i++){
      toRecipientAddress[i] = new InternetAddress(toEmails[i]);
    }
    String [] ccEmails = ccEmailAddress.split(",");
    InternetAddress[] ccRecipientAddress = new InternetAddress[ccEmails.length];
    for(int i=0;i<ccEmails.length;i++){
      ccRecipientAddress[i] = new InternetAddress(ccEmails[i]);
    }
    message.setRecipients(Message.RecipientType.TO,toRecipientAddress);
    message.setRecipients(Message.RecipientType.CC,ccRecipientAddress);
    message.setSubject(subject);
    Multipart multipart = new MimeMultipart();
    BodyPart bodyPart = new MimeBodyPart();
    bodyPart.setText( emailContent);
    multipart.addBodyPart(bodyPart);
    if(workBookBytes != null) {
      MimeBodyPart attachmentPart = new MimeBodyPart();
      attachmentPart.setDataHandler(new DataHandler(new ByteArrayDataSource(workBookBytes,EMAIL_ATTACHMENT_TYPE)));
      attachmentPart.setFileName(fileName);
      multipart.addBodyPart(attachmentPart);
    }
    message.setContent(multipart);
    javaMailSender.send(message);
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
