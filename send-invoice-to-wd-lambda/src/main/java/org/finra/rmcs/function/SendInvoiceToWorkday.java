package org.finra.rmcs.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.exception.InvoiceWdException;
import org.finra.rmcs.service.InvoiceWdService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SendInvoiceToWorkday implements Function<Map<String, Object>, String> {

  private final InvoiceWdService invoiceWdService;

  /**
   * Applies this function to the given argument.
   *
   * @param input the function argument
   * @return the function result
   */
  @Override
  public String apply(Map<String, Object> input) {
    UUID uuid = UUID.randomUUID();
    String correlationId = uuid.toString();
    String methodName =
        new StringBuilder()
            .append(Constants.CLASS)
            .append(this.getClass().getSimpleName())
            .append(Constants.SPACE)
            .append(Constants.METHOD)
            .append(Thread.currentThread().getStackTrace()[1].getMethodName())
            .append(Constants.SPACE)
            .append(Constants.CORRELATION_ID)
            .append(correlationId)
            .append(Constants.SPACE)
            .append(Constants.INPUT)
            .append(input)
            .toString();
    log.info("{} message: method entry", methodName);

    try {
      boolean dryRun =
          Boolean.parseBoolean(input.getOrDefault(Constants.DRY_RUN, Constants.FALSE).toString());
      log.info("dryRun: {}", dryRun);
      if (dryRun) {
        log.info("dry run mode");
        input.put(Constants.DRY_RUN, "success");
        return new ObjectMapper().writeValueAsString(input);
      }

      boolean testing =
          Boolean.parseBoolean(input.getOrDefault(Constants.TESTING, Constants.FALSE).toString());
      if (testing) {
        log.info("{} message: testing : {}", methodName, true);
        return handleTestOperations(input);
      }

      String revenueStream = null;

      if (input.get(Constants.REVENUE_STREAM) != null) {
        revenueStream = input.get(Constants.REVENUE_STREAM).toString();
      }
      invoiceWdService.sentReceivableInvoiceToWd(correlationId, revenueStream);
    } catch (Exception exception) {
      log.error(
          "{} message:Exception occurred when processing message to write outbound file to WorkDay {}",
          methodName,
          ExceptionUtils.getStackTrace(exception));
      throw new InvoiceWdException(correlationId + Constants.SPACE + ExceptionUtils.getStackTrace(exception));
    }
    return correlationId;
  }

  /// CLOVER:OFF
  private String handleTestOperations(Map<String, Object> input) {
    return invoiceWdService.testOperationsForExternalBucket(input);
  }
  /// CLOVER:ON
}
