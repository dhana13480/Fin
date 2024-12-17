package org.finra.rmcs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry(proxyTargetClass = true)
@SpringBootApplication
public class BatchFileDMReceivableFileLambdaApplication {

  public static void main(String[] args) {
    SpringApplication.run(BatchFileDMReceivableFileLambdaApplication.class, args);
  }
}
