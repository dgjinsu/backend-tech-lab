package com.example.product.global.kafka;

import com.example.product.domain.product.dto.message.ProductReduceStockRequest;
import com.example.product.domain.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductConsumer {
    private final ObjectMapper objectMapper;
    private final ProductService productService;

    @KafkaListener(topics = "product-stock-reduce", groupId = "order-group")
    public void consume(String message) {
        try {
            ProductReduceStockRequest request = objectMapper.readValue(message, ProductReduceStockRequest.class);

            productService.reduceProductStock(request);
        } catch (Exception e) {
            log.error("Failed to process message: {}", message, e);
        }
    }
}
