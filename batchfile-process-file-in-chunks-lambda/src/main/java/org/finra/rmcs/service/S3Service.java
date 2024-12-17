package org.finra.rmcs.service;

import java.util.List;

public interface S3Service {
  List<String> retrieveS3ObjectInRange(String s3Location, String range);

  long getFileSize(String s3Location);
}
