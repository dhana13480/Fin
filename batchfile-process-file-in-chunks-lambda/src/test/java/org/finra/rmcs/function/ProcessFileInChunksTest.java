package org.finra.rmcs.function;

import static org.mockito.ArgumentMatchers.anyString;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.service.impl.S3ServiceImpl;
import org.finra.rmcs.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class ProcessFileInChunksTest {

  @InjectMocks
  private ProcessFileInChunks processFileInChunks;

  @Mock
  private S3ServiceImpl s3Service;

  @Test
  public void testProcessFileInChunks_FirstTime() throws Exception {
    String receivables = TestUtil.getResourceFileContents("/receivables.jsonl");
    long expectedFileSize = 3096L;
    Map<String, Object> input = new HashMap<>();
    input.put(Constants.FILE_URL_KEY, "testS3");
    Mockito.when(s3Service.getFileSize(anyString())).thenReturn(expectedFileSize);
    Mockito.when(s3Service.retrieveS3ObjectInRange(anyString(), anyString()))
        .thenReturn(Arrays.asList(receivables));
    Map<String, Object> map = processFileInChunks.apply(input);
    Assertions.assertEquals(expectedFileSize, map.get(Constants.FILE_SIZE_KEY));
    Assertions.assertEquals(false, map.get(Constants.FINISHED_KEY));
    Assertions.assertEquals(2016L, map.get(Constants.BYTE_PROCESSED_KEY));
  }

  @Test
  public void testProcessFileInChunks_AfterFirst() throws Exception {
    String receivables = TestUtil.getResourceFileContents("/receivables.jsonl");
    long expectedFileSize = 4032L;
    Map<String, Object> input = new HashMap<>();
    input.put(Constants.FILE_URL_KEY, "testS3");
    input.put(Constants.FILE_SIZE_KEY, expectedFileSize);
    input.put(Constants.BYTE_PROCESSED_KEY, 2016L);
    Mockito.when(s3Service.retrieveS3ObjectInRange(anyString(), anyString()))
        .thenReturn(Arrays.asList(receivables));
    Map<String, Object> map = processFileInChunks.apply(input);
    Assertions.assertEquals(expectedFileSize, map.get(Constants.FILE_SIZE_KEY));
    Assertions.assertEquals(true, map.get(Constants.FINISHED_KEY));
    Assertions.assertEquals(expectedFileSize, map.get(Constants.BYTE_PROCESSED_KEY));
  }
}
