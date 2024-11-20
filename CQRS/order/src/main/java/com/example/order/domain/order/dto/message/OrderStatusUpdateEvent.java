package com.example.order.domain.order.dto.message;

import com.example.order.domain.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderStatusUpdateEvent {
    private Long orderId;
    private OrderStatus orderStatus;
}
