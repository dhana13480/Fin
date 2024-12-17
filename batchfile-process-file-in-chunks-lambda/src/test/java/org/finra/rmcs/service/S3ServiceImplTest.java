package org.finra.rmcs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.finra.rmcs.service.impl.S3ServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

@ExtendWith(MockitoExtension.class)
public class S3ServiceImplTest {

  private final String s3Location =
      "https://4652-5751-2377-datamgt-rmcs-kms.s3.amazonaws.com/rmcs-apibi/dapi/billing/jsonl/rmcs-apibi-receivables-in/schm-v0/data-v0/upload-dt%3D2023-03-14/uuid%3D0c13d282-ea9f-4501-a049-0a4a2993bb42/apibi_2023-03-14T18-08-09-853Z.jsonl";
  @InjectMocks
  private S3ServiceImpl s3Service;
  @Mock
  private S3Client s3Client;
  @Mock
  private ResponseBytes responseBytes;

  @Test
  public void testRetrieveS3ObjectInRange() {
    String expectedString = "testResult";
    List<String> expected = Arrays.asList(expectedString);
    Mockito.when(
            s3Client.getObject(
                Mockito.any(GetObjectRequest.class),
                ArgumentMatchers
                    .<ResponseTransformer<
                        GetObjectResponse, ResponseInputStream<GetObjectResponse>>>
                        any()))
        .then(
            invocation -> {
              GetObjectRequest objectRequest = invocation.getArgument(0);
              assertEquals("4652-5751-2377-datamgt-rmcs-kms", objectRequest.bucket());
              assertEquals(
                  "rmcs-apibi/dapi/billing/jsonl/rmcs-apibi-receivables-in/schm-v0/data-v0/upload-dt%3D2023-03-14/uuid%3D0c13d282-ea9f-4501-a049-0a4a2993bb42/apibi_2023-03-14T18-08-09-853Z.jsonl",
                  objectRequest.key());
              return responseBytes;
            });
    Mockito.when(responseBytes.asUtf8String()).thenReturn(expectedString);
    List<String> actual = s3Service.retrieveS3ObjectInRange(s3Location, "bytes=1-2");
    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void testGetFileSize() {
    long expected = 1024L;
    HeadObjectResponse headObjectResponse =
        HeadObjectResponse.builder().contentLength(expected).build();
    Mockito.when(s3Client.headObject(Mockito.any(HeadObjectRequest.class)))
        .thenReturn(headObjectResponse);
    long actual = s3Service.getFileSize(s3Location);
    Assertions.assertEquals(expected, actual);
  }
}
