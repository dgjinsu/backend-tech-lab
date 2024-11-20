package com.example.orderquery.global.kafka;

import com.example.orderquery.domain.orderquery.dto.message.OrderCreatedEvent;
import com.example.orderquery.domain.orderquery.entity.OrderQueryModel;
import com.example.orderquery.domain.orderquery.repository.OrderQueryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final OrderQueryRepository orderQueryRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-created-events", groupId = "order-query-group")
    public void handleOrderCreated(String message) {
        try {
            // JSON 역직렬화
            OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);

            // Query용 데이터 저장
            OrderQueryModel queryModel = new OrderQueryModel(
                event.getOrderId(),
                event.getOrderTime(),
                event.getProductId(),
                event.getQuantity(),
                event.getProductName(),
                event.getProductPrice()
            );
            orderQueryRepository.save(queryModel);

            log.info("OrderQueryModel updated: {}", queryModel);
        } catch (Exception e) {
            log.error("Failed to process order-created event", e);
        }
    }
}
