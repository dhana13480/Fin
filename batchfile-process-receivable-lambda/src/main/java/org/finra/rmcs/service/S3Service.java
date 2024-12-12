package org.finra.rmcs.service;

public interface S3Service {
  String retrieveS3ObjectInRange(String s3Location, String range);
}
