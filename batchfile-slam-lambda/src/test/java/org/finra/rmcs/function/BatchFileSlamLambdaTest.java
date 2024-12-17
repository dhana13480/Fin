package org.finra.rmcs.function;

import static org.mockito.ArgumentMatchers.anyString;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.service.impl.BatchFileSlamServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BatchFileSlamLambdaTest {

  @InjectMocks
  private BatchFileSlamLambda batchFileSlamLambda;

  @Mock
  private BatchFileSlamServiceImpl batchFileSlamService;

  @Test
  public void testSlamStartEvent() {
    Map<String, Object> input = new HashMap<>();
    input.put(
        Constants.BIZ_OBJ_STATUS_CHANGE_EVENT,
        "{\"businessObjectDataKey\":{\"namespace\":\"RMCS-APIBI\",\"businessObjectDefinitionName\":\"RMCS-APIBI-RECEIVABLES-IN\",\"businessObjectFormatUsage\":\"BILLING\",\"businessObjectFormatFileType\":\"JSONL\",\"businessObjectFormatVersion\":0,\"partitionValue\":\"2023-04-14\",\"subPartitionValues\":[\"75b840d8-b7bc-4b45-a049-28640f495348\"],\"businessObjectDataVersion\":0},\"eventDate\":\"2023-04-14T14:23:36.266-04:00\",\"newBusinessObjectDataStatus\":\"VALID\",\"oldBusinessObjectDataStatus\":\"UPLOADING\",\"attributes\":{\"revenue_stream\":\"APIBI\"}}");
    Map<String, Object> returnMap = batchFileSlamLambda.apply(input);
    Mockito.verify(batchFileSlamService).sendSlamEvent(anyString());
    Assertions.assertNotNull(returnMap.get(Constants.SLAM_ID));
  }

  @Test
  public void testSlamEndEvent() {
    Map<String, Object> input = new HashMap<>();
    String slamId = UUID.randomUUID().toString();
    input.put(Constants.SLAM_ID, slamId);
    input.put(Constants.REVENUE_STREAM, "APIBI");
    Map<String, Object> returnMap = batchFileSlamLambda.apply(input);
    Mockito.verify(batchFileSlamService).sendSlamEvent(anyString());
    Assertions.assertEquals(slamId, returnMap.get(Constants.SLAM_ID));
  }

  @Test
  public void testSlamEndEvent_dryRun() {
    Map<String, Object> input = new HashMap<>();
    input.put(Constants.DRY_RUN, "true");
    Assertions.assertEquals("success", batchFileSlamLambda.apply(input).get(Constants.DRY_RUN));
  }

  @Test
  public void testSlamEndEvent_exception() {
    Map<String, Object> input = new HashMap<>();
    String slamId = UUID.randomUUID().toString();
    input.put(Constants.SLAM_ID, slamId);
    Assertions.assertThrows(RuntimeException.class, () -> batchFileSlamLambda.apply(input));
  }
}
