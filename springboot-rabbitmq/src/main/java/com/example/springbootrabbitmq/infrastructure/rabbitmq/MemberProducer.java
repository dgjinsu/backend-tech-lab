package com.example.springbootrabbitmq.infrastructure.rabbitmq;

import com.example.springbootrabbitmq.infrastructure.rabbitmq.config.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberProducer {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;

    private static final String routingKey = "key1";

    public void sendMemberName(String message) {
        rabbitTemplate.convertAndSend(rabbitMQProperties.getExchangeName(), routingKey, message);
        log.info("발송 성공");
    }
}
