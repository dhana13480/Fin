package org.finra.rmcs.service.impl;

import com.amazonaws.services.s3.AmazonS3URI;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@Slf4j
@Service
public class S3ServiceImpl implements S3Service {

  private final S3Client s3Client;

  @Autowired
  public S3ServiceImpl(S3Client s3Client) {
    this.s3Client = s3Client;
  }

  @Override
  public String retrieveS3ObjectInRange(String s3Location, String range) {
    AmazonS3URI amazonS3URI = new AmazonS3URI(s3Location);
    log.info(
        "Getting S3 Object with bucket {} and key: {}",
        amazonS3URI.getBucket(),
        amazonS3URI.getKey());
    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder()
            .bucket(amazonS3URI.getBucket())
            .key(amazonS3URI.getKey())
            .range(range)
            .build();
    String data =
        s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes()).asUtf8String();
    log.info("Retrieved {} data from S3 location {}", range, s3Location);
    return data;
  }
}
