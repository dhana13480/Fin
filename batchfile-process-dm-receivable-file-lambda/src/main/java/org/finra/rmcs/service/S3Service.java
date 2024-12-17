package org.finra.rmcs.service;

public interface S3Service {
  String retrieveS3Object(String s3Location);

  long getFileSize(String s3Location);
}
