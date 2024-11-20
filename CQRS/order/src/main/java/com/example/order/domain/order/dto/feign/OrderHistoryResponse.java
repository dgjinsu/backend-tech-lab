package com.example.order.domain.order.dto.feign;

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
}
