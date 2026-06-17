package com.example.objectstorage.config;

import java.net.URI;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.objectstorage.storage.Backend;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * 백엔드별 S3Client 를 만들어 Map&lt;Backend, S3Client&gt; 하나로 등록한다.
 *
 * 핵심: SeaweedFS 든 Garage 든 "S3 호환"이므로 클라이언트 코드는 완전히 동일하고
 * endpoint / region / credentials 만 다르다. 이것이 이 PoC가 보여주려는 가치.
 *
 *  - endpointOverride : AWS 대신 우리 로컬 스토리지를 바라보게 한다.
 *  - forcePathStyle(true) : 두 백엔드 모두 path-style(http://host/bucket/key)로 접근.
 *    endpoint 가 IP:port 라 virtual-host style(http://bucket.host/...)은 DNS가 안 풀려 실패한다.
 */
@Configuration
public class S3ClientConfig {

    @Bean
    public Map<Backend, S3Client> s3Clients(StorageProperties props) {
        Map<Backend, S3Client> clients = new EnumMap<>(Backend.class);
        for (Backend backend : Backend.values()) {
            StorageProperties.BackendProps p = props.getBackends().get(backend.key());
            if (p == null) {
                continue; // 설정에 없는 백엔드는 건너뜀
            }
            clients.put(backend, S3Client.builder()
                    .endpointOverride(URI.create(p.getEndpoint()))
                    .region(Region.of(p.getRegion()))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(p.getAccessKey(), p.getSecretKey())))
                    .forcePathStyle(true)
                    // AWS SDK v2.30+ 는 기본으로 CRC32 flexible checksum 을 켜고 본문을 aws-chunked 로
                    // 서명한다. Garage 는 이 페이로드 서명을 거부해 "Invalid payload signature(400)" 가 난다.
                    // WHEN_REQUIRED 로 낮추면 일반 서명 PUT 이 되어 SeaweedFS/Garage 모두 정상 동작한다.
                    .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                    .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
                    .build());
        }
        return clients;
    }
}
