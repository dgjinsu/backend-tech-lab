package com.example.orderquery.domain.orderquery.dto;

import com.example.orderquery.domain.orderquery.entity.OrderQueryModel;
import com.example.orderquery.domain.orderquery.entity.OrderStatus;
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
    private OrderStatus orderStatus;
    private LocalDateTime orderTime;
    private Long productId;
    private Integer quantity;
    private String productName; // Product 이름 (Product 정보 포함)
    private Integer productPrice; // Product 가격

    public static OrderResponse from(OrderQueryModel orderQueryModel) {
        return OrderResponse.builder()
            .orderId(orderQueryModel.getOrderId())
            .orderStatus(orderQueryModel.getStatus())
            .orderTime(orderQueryModel.getOrderTime())
            .productId(orderQueryModel.getProductId())
            .quantity(orderQueryModel.getQuantity())
            .productName(orderQueryModel.getProductName())
            .productPrice(orderQueryModel.getProductPrice())
            .build();
    }
}
