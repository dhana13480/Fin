package org.finra.rmcs.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.constants.Constants;

@Slf4j
public class Util {

  public static String getNextRange(long bytesProcessed, long filesize) {
    return String.format(
        Constants.BYTES_RANGE_FORMAT,
        bytesProcessed,
        Math.min(bytesProcessed + Constants.CHUNK_SIZE, filesize));
  }

  public static List<String> stripLastJsonLine(
      List<String> jsonLines, long upperLimit, long fileSize) {
    return upperLimit >= fileSize ? jsonLines : jsonLines.subList(0, jsonLines.size() - 1);
  }

  public static long calculateTotalBytesOfProcessedJsonLines(
      List<String> jsonLines, long bytesProcessed, long fileSize) {
    for (String line : jsonLines) {
      bytesProcessed += (line.getBytes().length + System.lineSeparator().getBytes().length);
    }
    return bytesProcessed - fileSize == System.lineSeparator().getBytes().length
        ? bytesProcessed - 1
        : bytesProcessed;
  }

  @SneakyThrows
  public static void populateInputForIterations(
      Map<String, Object> returnMap, long bytesProcessed, long fileSize, List<String> bytesRanges) {
    returnMap.put(Constants.BYTE_PROCESSED_KEY, bytesProcessed);
    returnMap.put(Constants.FILE_SIZE_KEY, fileSize);
    returnMap.put(Constants.FINISHED_KEY, fileSize - bytesProcessed == 0);
    ObjectMapper objectMapper = new ObjectMapper();
    ArrayNode arrayNode = objectMapper.createArrayNode();
    bytesRanges.forEach(arrayNode::add);
    returnMap.put(Constants.RECEIVABLE_KEY, arrayNode);
  }

  public static List<String> convertBytesRangeQuery(List<String> jsonLines, long startedByte) {
    List<String> bytes = new ArrayList<>();
    for (String json : jsonLines) {
      startedByte =
          startedByte == 0 ? startedByte : startedByte + System.lineSeparator().getBytes().length;
      bytes.add(
          String.format(
              Constants.BYTES_RANGE_FORMAT, startedByte, json.getBytes().length + startedByte));
      startedByte += json.getBytes().length;
    }
    return bytes;
  }
}
