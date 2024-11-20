package com.example.orderquery.global.kafka;

import com.example.orderquery.domain.orderquery.dto.message.OrderCreatedEvent;
import com.example.orderquery.domain.orderquery.dto.message.OrderStatusUpdateEvent;
import com.example.orderquery.domain.orderquery.service.OrderQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final ObjectMapper objectMapper;
    private final OrderQueryService orderQueryService;

    @KafkaListener(topics = "order-created-events", groupId = "order-query-group")
    public void handleOrderCreated(String message) {
        try {
            // JSON 역직렬화
            OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);


            orderQueryService.saveOrderQuery(event);

            log.info("OrderQueryModel updated: {}", event.getOrderStatus());
        } catch (Exception e) {
            log.error("Failed to process order-created event", e);
        }
    }

    @KafkaListener(topics = "order-status-updated-events", groupId = "order-query-group")
    public void consumeOrderStatusUpdateEvent(String message) {
        try {
            // JSON 메시지를 OrderStatusUpdateEvent로 역직렬화
            OrderStatusUpdateEvent event = objectMapper.readValue(message, OrderStatusUpdateEvent.class);

            // 이벤트 처리 로직 호출
            orderQueryService.updateOrderQueryStatus(event);

            log.info("Consumed and processed OrderStatusUpdateEvent: {}", event);
        } catch (Exception e) {
            log.error("Failed to process OrderStatusUpdateEvent", e);
        }
    }
}
