package com.example.rabbitmqparallel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumer")
@Data
public class ConsumerProperties {
    private int concurrency = 5;
    private int maxConcurrency = 5;
}
