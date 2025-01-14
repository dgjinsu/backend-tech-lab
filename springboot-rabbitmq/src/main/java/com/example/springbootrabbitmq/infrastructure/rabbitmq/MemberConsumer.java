package com.example.springbootrabbitmq.infrastructure.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MemberConsumer {

    private static final String QUEUE_NAME = "queue1";

    /**
     * example-queue로 전달된 메시지를 소비하는 메서드
     */
    @RabbitListener(queues = QUEUE_NAME)
    public void consumeMessage(@Payload String message) {
        log.info("Received message from RabbitMQ: {}", message);

        // 메시지 처리 로직 작성
        processMessage(message);
    }

    /**
     * 메시지 처리 로직을 구현하는 메서드
     */
    private void processMessage(String message) {
        log.info("Processing message: {}", message);
    }
}