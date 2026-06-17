package com.example.objectstorage.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * application.yml 의 storage.* 바인딩.
 *
 * storage.backends.&lt;name&gt;.{endpoint, region, accessKey, secretKey, bucket}
 * 여기서 name 은 Backend enum 의 key()(소문자)와 일치한다: seaweedfs, garage
 */
@Component
@ConfigurationProperties(prefix = "storage")
@Getter
@Setter
public class StorageProperties {

    /** key = 백엔드 이름(소문자). */
    private Map<String, BackendProps> backends = new LinkedHashMap<>();

    @Getter
    @Setter
    public static class BackendProps {
        private String endpoint;
        private String region;
        private String accessKey;
        private String secretKey;
        private String bucket;
    }
}
