package org.finra.rmcs.constants;

public class Constants {

  public static final String CLASS = "class: ";
  public static final String METHOD = "method: ";
  public static final String CORRELATION_ID = "correlationId: ";
  public static final String INPUT = "input: ";
  public static final String FILE_URL_KEY = "file_url";
  public static final String FILE_SIZE_KEY = "fileSize";
  public static final String BYTE_PROCESSED_KEY = "bytesProcessed";
  public static final String FINISHED_KEY = "finished";
  public static final String RECEIVABLE_KEY = "receivable";
  public static final String BYTES_RANGE_FORMAT = "bytes=%s-%s";
  public static final long CHUNK_SIZE = 1024L * 4096;

  public static final String DRY_RUN = "dryRun";
  public static final String FALSE = "false";

  private Constants() {
    throw new IllegalStateException(" all are static methods ");
  }
}
