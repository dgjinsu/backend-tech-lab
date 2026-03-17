package com.example.rabbitmqparallel.producer;

import com.example.rabbitmqparallel.dto.TaskMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.exchange-name}")
    private String exchangeName;

    @Value("${queue.routing-key}")
    private String routingKey;

    public List<TaskMessage> publishBurst(int count) {
        List<TaskMessage> messages = new ArrayList<>();
        long publishTime = System.currentTimeMillis();

        for (int i = 1; i <= count; i++) {
            TaskMessage msg = TaskMessage.builder()
                    .messageId(UUID.randomUUID().toString())
                    .sequenceNumber(i)
                    .payload("test-payload-" + i)
                    .publishedAt(publishTime)
                    .build();

            rabbitTemplate.convertAndSend(exchangeName, routingKey, msg);
            messages.add(msg);
        }

        log.info("Published {} messages in burst (exchange={}, routingKey={})",
                count, exchangeName, routingKey);
        return messages;
    }
}
