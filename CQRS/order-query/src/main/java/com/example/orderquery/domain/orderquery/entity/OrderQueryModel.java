package com.example.orderquery.domain.orderquery.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderQueryModel {
    @Id
    private ObjectId id;
    private Long orderId;
    private OrderStatus status;
    private LocalDateTime orderTime;
    private Long productId;
    private Integer quantity;
    private String productName; // Product 이름 (Product 정보 포함)
    private Integer productPrice; // Product 가격

    public OrderQueryModel(Long orderId, OrderStatus status, LocalDateTime orderTime,
        Long productId,
        Integer quantity, String productName, Integer productPrice) {
        this.orderId = orderId;
        this.status = status;
        this.orderTime = orderTime;
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public void updateStatus(OrderStatus orderStatus) {
        this.status = orderStatus;
    }
}