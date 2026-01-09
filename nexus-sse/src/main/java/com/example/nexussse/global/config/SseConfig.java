package com.example.nexussse.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sse")
@Getter
@Setter
public class SseConfig {

    private long timeout = 60000L;

    private long reconnectTime = 3000L;
}
