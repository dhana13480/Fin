package org.finra.rmcs.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.finra.rmcs.constants.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class UtilTest {

  @Test
  public void testGetNextRange_whenFileSizeSmallerThanChunkSize() {
    Assertions.assertEquals("bytes=0-256", Util.getNextRange(0, 256));
  }

  @Test
  public void testGetNextRange_whenFileSizeGreaterThanChunkSize() {
    Assertions.assertEquals(
        "bytes=4194304-8388608", Util.getNextRange(Constants.CHUNK_SIZE, Constants.CHUNK_SIZE * 2));
  }

  @Test
  public void testStripLastJsonLine_whenUpperLimitGreaterThanFileSize() {
    List<String> list = Arrays.asList("test1", "test2");
    List<String> actual = Util.stripLastJsonLine(list, 2000L, 1000L);
    Assertions.assertEquals(2, actual.size());
  }

  @Test
  public void testStripLastJsonLine_whenUpperLimitLessThanFileSize() {
    List<String> list = Arrays.asList("test1", "test2");
    List<String> actual = Util.stripLastJsonLine(list, 1000L, 2000L);
    Assertions.assertEquals(1, actual.size());
  }

  @Test
  public void testCalculateTotalBytesOfProcessedJsonLines() {
    List<String> list = Arrays.asList("test1", "test2");
    long byteProcessed = Util.calculateTotalBytesOfProcessedJsonLines(list, 0, 200);
    Assertions.assertEquals(12, byteProcessed);
  }

  @Test
  public void testCalculateTotalBytesOfProcessedJsonLines_whenLastChunks() {
    List<String> list = Arrays.asList("test1", "test2");
    long byteProcessed = Util.calculateTotalBytesOfProcessedJsonLines(list, 0, 11);
    Assertions.assertEquals(11, byteProcessed);
  }

  @Test
  public void testPopulateInputForIterations() throws Exception {
    List<String> list = Arrays.asList(TestUtil.getResourceFileContents("/receivables.jsonl"));
    Map<String, Object> returnMap = new HashMap<>();
    Util.populateInputForIterations(returnMap, 2023L, 2023L, list);
    Assertions.assertTrue(Boolean.parseBoolean(returnMap.get(Constants.FINISHED_KEY).toString()));
  }

  @Test
  public void testConvertBytesRangeQuery_start0() throws Exception {
    List<String> list = Arrays.asList(TestUtil.getResourceFileContents("/receivables.jsonl"));
    List<String> bytesRange = Util.convertBytesRangeQuery(list,0);
    Assertions.assertEquals("bytes=0-2015", bytesRange.get(0));
  }

  @Test
  public void testConvertBytesRangeQuery() throws Exception {
    List<String> list = Arrays.asList(TestUtil.getResourceFileContents("/receivables.jsonl"));
    List<String> bytesRange = Util.convertBytesRangeQuery(list,800);
    Assertions.assertEquals("bytes=801-2816", bytesRange.get(0));
  }
}
