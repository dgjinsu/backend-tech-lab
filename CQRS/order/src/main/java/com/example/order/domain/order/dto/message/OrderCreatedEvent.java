package com.example.order.domain.order.dto.message;

import com.example.order.domain.order.entity.OrderStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderCreatedEvent {
    private Long orderId;
    private OrderStatus orderStatus;
    private LocalDateTime orderTime;
    private Long productId;
    private Integer quantity;
    private String productName; // Product 이름 (Product 정보 포함)
    private Integer productPrice; // Product 가격
}
