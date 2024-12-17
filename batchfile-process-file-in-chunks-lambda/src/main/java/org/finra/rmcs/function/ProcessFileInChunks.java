package org.finra.rmcs.function;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.service.impl.S3ServiceImpl;
import org.finra.rmcs.util.Util;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ProcessFileInChunks implements Function<Map<String, Object>, Map<String, Object>> {

  private final S3ServiceImpl s3Service;

  @Override
  public Map<String, Object> apply(Map<String, Object> input) {
    String correlationId = UUID.randomUUID().toString();

    String methodName =
        Constants.CLASS
            + this.getClass().getSimpleName()
            + " "
            + Constants.METHOD
            + Thread.currentThread().getStackTrace()[1].getMethodName()
            + " "
            + Constants.CORRELATION_ID
            + correlationId
            + " "
            + Constants.INPUT
            + input;
    log.info("{} message: method entry", methodName);

    boolean dryRun =
        Boolean.parseBoolean(input.getOrDefault(Constants.DRY_RUN, Constants.FALSE).toString());
    log.info("dryRun: {}", dryRun);
    if (dryRun) {
      log.info("dry run mode");
      input.put(Constants.DRY_RUN, "success");
      return input;
    }

    String s3Url = input.get(Constants.FILE_URL_KEY).toString();
    long fileSize = getFileSize(input, s3Url);
    long bytesProcessed = getProcessedSize(input);
    List<String> jsonLines =
        s3Service.retrieveS3ObjectInRange(s3Url, Util.getNextRange(bytesProcessed, fileSize));
    jsonLines =
        Util.stripLastJsonLine(jsonLines, getUpperLimit(bytesProcessed, fileSize), fileSize);
    List<String> bytesRanges = Util.convertBytesRangeQuery(jsonLines, bytesProcessed);
    bytesProcessed =
        Util.calculateTotalBytesOfProcessedJsonLines(jsonLines, bytesProcessed, fileSize);
    Util.populateInputForIterations(input, bytesProcessed, fileSize, bytesRanges);
    return input;
  }

  private long getUpperLimit(long bytesProcessed, long fileSize) {
    long upperLimit = bytesProcessed + Constants.CHUNK_SIZE;
    return Math.min(upperLimit, fileSize);
  }

  private long getFileSize(Map<String, Object> input, String s3Url) {
    String fileSize = input.getOrDefault(Constants.FILE_SIZE_KEY, StringUtils.EMPTY).toString();
    return StringUtils.isBlank(fileSize) ? s3Service.getFileSize(s3Url) : Long.parseLong(fileSize);
  }

  private long getProcessedSize(Map<String, Object> input) {
    String bytesProcessed =
        input.getOrDefault(Constants.BYTE_PROCESSED_KEY, StringUtils.EMPTY).toString();
    return StringUtils.isBlank(bytesProcessed)
        ? Long.parseLong("0")
        : Long.parseLong(bytesProcessed);
  }
}
