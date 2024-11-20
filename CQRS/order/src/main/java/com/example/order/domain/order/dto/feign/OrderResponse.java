package com.example.order.domain.order.dto.feign;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderResponse {
    private Long orderId;
    private LocalDateTime orderTime;
    private Long productId;
    private Integer quantity;
    private String productName; // Product 이름 (Product 정보 포함)
    private Integer productPrice; // Product 가격
}
