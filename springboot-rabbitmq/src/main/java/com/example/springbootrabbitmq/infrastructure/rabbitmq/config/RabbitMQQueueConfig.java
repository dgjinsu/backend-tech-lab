package com.example.springbootrabbitmq.infrastructure.rabbitmq.config;

import lombok.Data;

@Data
public class RabbitMQQueueConfig {
    private String name;
    private String routingKey;
}