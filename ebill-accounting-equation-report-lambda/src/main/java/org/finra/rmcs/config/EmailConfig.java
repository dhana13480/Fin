package org.finra.rmcs.config;

import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@ComponentScan(basePackages = {"org.finra.rmcs.common"})
@Slf4j
@EnableConfigurationProperties
public class EmailConfig {

  private static final String MAIL_TRANSFER_PROTOCOL = "mail.transport.protocol";
  private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
  private static final String MAIL_SMTP_TLS_OPTION = "mail.smtp.starttls.enable";
  private static final String MAIL_SMTP_DEBUG = "mail.debug";

  @Value("${email.host:mailhost.aws.finra.org}")
  private String emailHost;

  @Value("${email.port:25}")
  private int emailPort;

  @Value("${email.debug:false}")
  private String mailDebug;

  @Value("${smtp.auth:false}")
  private String smtpAuth;

  @Value("${smtp.tls.option:true}")
  private String tlsEnable;

  @Value("${mail.transfer.protocol:smtp}")
  private String transferProtocol;

  @Bean
  public JavaMailSender getJavaMailSender() {
    log.info("email host: {}", emailHost);
    log.info("email port: {}", emailPort);
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(emailHost);
    mailSender.setPort(emailPort);
    Properties props = mailSender.getJavaMailProperties();
    props.put(MAIL_TRANSFER_PROTOCOL, transferProtocol);
    props.put(MAIL_SMTP_AUTH, smtpAuth);
    props.put(MAIL_SMTP_TLS_OPTION, tlsEnable);
    props.put(MAIL_SMTP_DEBUG, mailDebug);

    return mailSender;
  }
}
