package com.example.server2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RabbitMQConsumer {

    @RabbitListener(queues = "test-queue")
    public void receiveMessage(String message) {
        log.info("Received message: {}", message);
    }
}