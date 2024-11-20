package com.example.order.domain.order.service;

import com.example.order.domain.order.dto.OrderSaveRequest;
import com.example.order.domain.order.dto.OrderStatusUpdateRequest;
import com.example.order.domain.order.dto.feign.ProductResponse;
import com.example.order.domain.order.dto.message.OrderCreatedEvent;
import com.example.order.domain.order.dto.message.OrderStatusUpdateEvent;
import com.example.order.domain.order.dto.message.ProductReduceStockRequest;
import com.example.order.domain.order.entity.Order;
import com.example.order.domain.order.entity.OrderProduct;
import com.example.order.domain.order.entity.OrderStatus;
import com.example.order.domain.order.repository.OrderProductRepository;
import com.example.order.domain.order.repository.OrderRepository;
import com.example.order.global.feign.ProductClient;
import com.example.order.global.kafka.OrderEventProducer;
import com.example.order.global.kafka.ProductProducer;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductClient productClient;
    private final ProductProducer productProducer;
    private final OrderEventProducer orderEventProducer;

    public void saveOrder(OrderSaveRequest request) {
        ProductResponse productResponse = productClient.getProducts(request.getProductId());

        Order order = Order.builder()
            .orderTime(LocalDateTime.now())
            .status(OrderStatus.COMPLETE)
            .build();
        orderRepository.save(order);

        // OrderProduct 객체 생성
        OrderProduct orderProduct = OrderProduct.builder()
            .quantity(request.getQuantity())
            .productId(productResponse.getProductId())
            .orderId(order.getId()) // orderId를 설정
            .build();
        orderProductRepository.save(orderProduct);

        // product 재고 감소
        productProducer.reduceStock("product-reduce", new ProductReduceStockRequest(
            productResponse.getProductId()));

        // Query 전용 Order Model 저장
        OrderCreatedEvent event = new OrderCreatedEvent(
            order.getId(),
            order.getStatus(),
            order.getOrderTime(),
            productResponse.getProductId(),
            request.getQuantity(),
            productResponse.getName(),
            productResponse.getPrice()
        );

        orderEventProducer.publishOrderCreatedEvent(event);
    }

    public void updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.updateOrderStatus(request.getOrderStatus());

        OrderStatusUpdateEvent event = new OrderStatusUpdateEvent(
            order.getId(),
            order.getStatus()
        );
        orderEventProducer.publishOrderUpdateEvent(event);
    }
}
