package com.example.order.domain.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderProduct {
    @Id
    @GeneratedValue
    @Column(name = "order_product_id")
    private Long id;

    private Integer quantity;
    private Long orderId;
    private Long productId;

    @Builder
    public OrderProduct(Integer quantity, Long orderId, Long productId) {
        this.quantity = quantity;
        this.orderId = orderId;
        this.productId = productId;
    }
}
