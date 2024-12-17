package org.finra.rmcs.utils;

import org.finra.rmcs.constants.Constants;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.Credentials;

public class S3Util {

  private S3Util() {
    throw new IllegalStateException("S3Util class");
  }

  public static S3Client getExternalS3Client(String assumeRoleArn) {
    AssumeRoleRequest assumeRoleRequest =
        AssumeRoleRequest.builder()
            .durationSeconds(Constants.EXTERNAL_SESSION_DURATION)
            .roleArn(assumeRoleArn)
            .roleSessionName(Constants.EXTERNAL_SESSION_NAME)
            .build();

    Credentials credentials =
        StsClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build()
            .assumeRole(assumeRoleRequest)
            .credentials();

    AwsSessionCredentials sessionCredentials =
        AwsSessionCredentials.create(
            credentials.accessKeyId(), credentials.secretAccessKey(), credentials.sessionToken());
    return S3Client.builder()
        .credentialsProvider(
            AwsCredentialsProviderChain.builder()
                .credentialsProviders(StaticCredentialsProvider.create(sessionCredentials))
                .build())
        .region(Region.US_EAST_1)
        .build();
  }

  public static S3Client getInternalS3Client() {
    return S3Client.builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .build();
  }
}
