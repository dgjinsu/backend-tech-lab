package com.example.order.domain.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OrderSaveRequest {
    private Long productId;
    private Integer quantity;
}
