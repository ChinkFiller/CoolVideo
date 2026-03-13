package com.ruoyi.common.utils.oss;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.time.Duration;

public class OssClientBuilder {
    public static S3Presigner createPresigner(Region region, String endpoint, String accessKey, String secretKey, Boolean isHttps) {
        return S3Presigner.builder()
                .region(region)
                .endpointOverride(isHttps? URI.create("https://"+endpoint):URI.create("http://"+endpoint))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }


    public static S3Client createS3Client(Region region, String endpoint, String accessKey, String secretKey,Boolean isHttps) {
        return S3Client.builder()
                .overrideConfiguration(
                        ClientOverrideConfiguration.builder()
                                .retryPolicy(RetryPolicy.none())
                                .build()
                )
                .httpClientBuilder(ApacheHttpClient.builder()
                        .connectionTimeout(Duration.ofMillis(500))
                        .socketTimeout(Duration.ofMillis(500))
                )
                .region(region)
                .endpointOverride(isHttps?URI.create("https://"+endpoint):URI.create("http://"+endpoint))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }
}
