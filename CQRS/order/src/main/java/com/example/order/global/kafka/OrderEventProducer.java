package com.example.order.global.kafka;

import com.example.order.domain.order.dto.message.OrderCreatedEvent;
import com.example.order.domain.order.dto.message.OrderStatusUpdateEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishOrderCreatedEvent(OrderCreatedEvent event) {
        try {
            // OrderCreatedEvent 객체를 JSON 문자열로 직렬화
            String message = objectMapper.writeValueAsString(event);

            // Kafka 토픽으로 메시지 전송
            kafkaTemplate.send("order-created-events", message);

            log.info("Published OrderCreatedEvent to Kafka: {}", message);
        } catch (Exception e) {
            log.error("Failed to publish OrderCreatedEvent", e);
            throw new RuntimeException("Failed to publish OrderCreatedEvent", e);
        }
    }

    public void publishOrderUpdateEvent(OrderStatusUpdateEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);

            // Kafka 토픽으로 메시지 전송
            kafkaTemplate.send("order-status-updated-events", message);

            log.info("Published OrderStatusUpdateEvent to Kafka: {}", message);
        } catch (Exception e) {
            log.error("Failed to publish OrderCreatedEvent", e);
            throw new RuntimeException("Failed to publish OrderCreatedEvent", e);
        }
    }

    public void publishOrderCreatedEventWithCDC(OrderCreatedEvent event) {
        try {
            // OrderCreatedEvent 객체를 JSON 문자열로 직렬화
            String message = objectMapper.writeValueAsString(event);

            // Kafka 토픽으로 메시지 전송
            kafkaTemplate.send("order-events", message); // "order-events"는 MongoDB Sink Connector와 연결된 토픽

            log.info("Published OrderCreatedEvent to Kafka: {}", message);
        } catch (Exception e) {
            log.error("Failed to publish OrderCreatedEvent", e);
            throw new RuntimeException("Failed to publish OrderCreatedEvent", e);
        }
    }
}