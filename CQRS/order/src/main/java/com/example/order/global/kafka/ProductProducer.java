package com.example.order.global.kafka;

import com.example.order.domain.order.dto.message.ProductReduceStockRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.Message;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void reduceStock(String topic, ProductReduceStockRequest request) {
        try {
            // 요청 객체를 JSON 문자열로 직렬화
            String message = objectMapper.writeValueAsString(request);

            // Kafka에 메시지 전송
            kafkaTemplate.send(topic, message);

            log.info("Sent message to topic {}: {}", topic, message);
        } catch (Exception e) {
            log.error("Failed to send message to topic {}: {}", topic, e.getMessage());
            throw new RuntimeException("Failed to send Kafka message", e);
        }
    }
}
