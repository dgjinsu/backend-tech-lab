package com.example.seaweedpoc.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * SeaweedFS의 S3 게이트웨이에 붙는 S3Client 빈 구성.
 *
 * 포인트는 두 가지:
 *  1) endpointOverride : AWS가 아니라 우리 SeaweedFS(localhost:8333)를 바라보게 한다.
 *  2) forcePathStyle(true) : SeaweedFS는 path-style 주소(http://host/bucket/key)만 지원한다.
 *     기본값인 virtual-host style(http://bucket.host/key)은 localhost에서 DNS가 안 풀려 실패한다.
 */
@Configuration
public class S3Config {

    @Value("${seaweedfs.s3.endpoint}")
    private String endpoint;

    @Value("${seaweedfs.s3.region}")
    private String region;

    @Value("${seaweedfs.s3.access-key}")
    private String accessKey;

    @Value("${seaweedfs.s3.secret-key}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .forcePathStyle(true) // SeaweedFS 필수
                .build();
    }
}
