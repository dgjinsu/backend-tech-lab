package com.example.order.domain.order.dto;

import com.example.order.domain.orderquery.entity.OrderQueryModel;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderHistoryResponse {
    List<OrderResponse> orderResponseList;

    public static Object from(OrderQueryModel orderQueryModel) {
        return null;
    }
}
