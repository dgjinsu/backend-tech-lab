package com.example.orderquery.domain.orderquery.dto.message;

import com.example.orderquery.domain.orderquery.entity.OrderStatus;
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
