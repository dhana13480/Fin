package org.finra.rmcs.function;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.dto.DmNotification;
import org.finra.rmcs.service.InvoiceFileSummaryService;
import org.finra.rmcs.utils.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class EbillFinappsNotificationProcessFunctionTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  @Mock
  InvoiceFileSummaryService invoiceFileSummaryService;
  @InjectMocks
  private EbillFinappsNotificationProcessFunction ebillFinappsNotificationProcessFunction;

  @BeforeEach
  public void init() throws Exception {
  }

  @Test
  void apply_dryRunTest() {
    String testQueueMessage = "{\"Message\" : \"{ \\\"dryRun\\\" }\" }";
    SQSEvent sqsEvent = Util.getSqsEvent(testQueueMessage);
    Map<String, Object> actual = ebillFinappsNotificationProcessFunction.apply(sqsEvent);
    Assertions.assertEquals("success", actual.get(Constants.DRY_RUN));
  }

  @Test
  void apply_healthTest() {
    String testQueueMessage = "{\"Message\" : \"{ \\\"health\\\" }\" }";
    SQSEvent sqsEvent = Util.getSqsEvent(testQueueMessage);
    Map<String, Object> actual = ebillFinappsNotificationProcessFunction.apply(sqsEvent);
    Assertions.assertEquals("success", actual.get(Constants.HEALTH_PAYLOAD));
  }

  @Test
  void apply_validMessageTest() throws IOException {
    String testQueueMessage = "{\"Message\" : \"{\\\"eventDate\\\": \\\"2023-11-01T04:31:41.883-04:00\\\", \\\"businessObjectDataKey\\\": { \\\"namespace\\\": \\\"FINAPPS_INT\\\", \\\"businessObjectDefinitionName\\\": \\\"INVOICE_PDF\\\", \\\"businessObjectFormatUsage\\\": \\\"PRC\\\", \\\"businessObjectFormatFileType\\\": \\\"PDF\\\", \\\"businessObjectFormatVersion\\\": 0, \\\"partitionValue\\\": \\\"ICE23103123843258\\\", \\\"businessObjectDataVersion\\\": 0 }, \\\"newBusinessObjectDataStatus\\\": \\\"UPLOADING\\\" }\" }";
    SQSEvent sqsEvent = Util.getSqsEvent(testQueueMessage);
    sqsEvent.getRecords().get(0).setMessageId("1234567");
    DmNotification notificationMessage = DmNotification.builder().build();
    Mockito.doNothing().when(invoiceFileSummaryService)
        .storeDmNotificationIntoInvoiceFileSummary("", notificationMessage);
    Map<String, Object> actual = ebillFinappsNotificationProcessFunction.apply(sqsEvent);
    Assertions.assertEquals(Constants.COMPLETION_STATUS_SUCCESS,
        actual.get(Constants.COMPLETION_STATUS));
    Assertions.assertEquals(Constants.COMPLETION_SUCCESS_DETAILS,
        actual.get(Constants.COMPLETION_DETAILS));
  }

  @Test()
  void apply_exceptionMessageTest() throws Exception {
    String testQueueMessage = "{\"Message\" : \"{\\\"eventDate\\\": \\\"2023-11-01T04:31:41.883-04:00\\\", \\\"businessObjectDataKey\\\": { \\\"namespace\\\": \\\"FINAPPS_INT\\\", \\\"businessObjectDefinitionName\\\": \\\"INVOICE_PDF\\\", \\\"businessObjectFormatUsage\\\": \\\"PRC\\\", \\\"businessObjectFormatFileType\\\": \\\"PDF\\\", \\\"businessObjectFormatVersion\\\": 0, \\\"partitionValue\\\": \\\"ICE23103123843258\\\", \\\"businessObjectDataVersion\\\": 0 }, \\\"newBusinessObjectDataStatus\\\": \\\"UPLOADING\\\" }\" }";
    SQSEvent sqsEvent = Util.getSqsEvent(testQueueMessage);
    Mockito.doThrow(new RuntimeException()).when(invoiceFileSummaryService)
        .storeDmNotificationIntoInvoiceFileSummary(Mockito.anyString(), Mockito.any());
    Map<String, Object> actual = ebillFinappsNotificationProcessFunction.apply(sqsEvent);
    Assertions.assertEquals(Constants.COMPLETION_STATUS_FAILED,
        actual.get(Constants.COMPLETION_STATUS));
    Assertions.assertEquals(Constants.COMPLETION_FAILED_DETAILS,
        actual.get(Constants.COMPLETION_DETAILS));

  }

}
