package org.finra.rmcs.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.service.InvoiceService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ConvertReceivableToInvoice implements Function<Map<String, Object>, String> {

  private final InvoiceService invoiceService;

  /**
   * Applies this function to the given argument.
   *
   * @return the function result
   */
  @Override
  @SneakyThrows
  public String apply(Map<String, Object> input) {
    UUID uuid = UUID.randomUUID();
    String correlationId = uuid.toString();
    String methodName =
        Constants.CLASS
            + this.getClass().getSimpleName()
            + " "
            + Constants.METHOD
            + Thread.currentThread().getStackTrace()[1].getMethodName()
            + " "
            + Constants.CORRELATION_ID
            + correlationId;
    log.info("{} message: method entry", methodName);

    boolean dryRun =
        Boolean.parseBoolean(input.getOrDefault(Constants.DRY_RUN, Constants.FALSE).toString());
    log.info("dryRun: {}", dryRun);
    if (dryRun) {
      log.info("dry run mode");
      input.put(Constants.DRY_RUN, "success");
      return new ObjectMapper().writeValueAsString(input);
    }
    invoiceService.convertReceivableToInvoice(correlationId);
    return correlationId;
  }
}
