package com.example.springbootrabbitmq.infrastructure.rabbitmq.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
@Data
public class RabbitMQProperties {

    private String host;
    private int port;
    private String username;
    private String password;
    private String exchangeName;
    private List<RabbitMQQueueConfig> queues;
}