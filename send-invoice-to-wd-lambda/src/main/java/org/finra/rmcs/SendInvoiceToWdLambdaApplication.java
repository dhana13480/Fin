package org.finra.rmcs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SendInvoiceToWdLambdaApplication {

  public static void main(String[] args) {
    SpringApplication.run(SendInvoiceToWdLambdaApplication.class, args);
  }
}
