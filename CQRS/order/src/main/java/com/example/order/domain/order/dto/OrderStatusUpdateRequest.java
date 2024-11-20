package com.example.order.domain.order.dto;

import com.example.order.domain.order.entity.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OrderStatusUpdateRequest {
    private OrderStatus orderStatus;
}
