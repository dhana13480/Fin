package org.finra.rmcs.constants;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EventTypeEnumTest {

  private static Stream<Arguments> prepareModuleAndStatus() {

    return Stream.of(
        Arguments.of("INVOICE_AUTOPAY", "DELETE_USER_INITIATE"),
        Arguments.of("INVOICE_AUTOPAY", "DELETE_PAYMENT_FAILED"),
        Arguments.of("INVOICE_AUTOPAY", "SETUP"));
  }

  @ParameterizedTest
  @MethodSource("prepareModuleAndStatus")
  void testFindbyPaymentTypeAndEventName(String module, String status) {
    String expectedValue = module + "_" + status;
    String actualValue = EventTypeEnum.findbyPaymentTypeAndEventName(module, status);
    Assertions.assertEquals(expectedValue, actualValue);
  }

  @Test
  void testFindbyPaymentTypeAndEventName_null() {
    String module = "INVOICE_AUTOPAY_TEST";
    String status = "SETUP";

    String actualValue = EventTypeEnum.findbyPaymentTypeAndEventName(module, status);
    Assertions.assertNull(actualValue);
  }

  @Test
  void testGetValue() {
    String expectedValue = "INVOICE_AUTOPAY_DELETE_SYSTEM_INITIATE";

    String actualValue = EventTypeEnum.INVOICE_AUTOPAY_DELETE_SYSTEM_INITIATE.getValue();
    Assertions.assertEquals(expectedValue, actualValue);
  }

}
