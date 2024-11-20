package com.example.order.domain.orderquery.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderQueryModel {
    @Id
    private Long orderId;
    private LocalDateTime orderTime;
    private Long productId;
    private Integer quantity;
    private String productName; // Product 이름 (Product 정보 포함)
    private Integer productPrice; // Product 가격
}