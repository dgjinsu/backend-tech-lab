package com.example.orderquery.domain.orderquery.service;

import com.example.orderquery.domain.orderquery.dto.OrderHistoryResponse;
import com.example.orderquery.domain.orderquery.dto.OrderResponse;
import com.example.orderquery.domain.orderquery.dto.message.OrderCreatedEvent;
import com.example.orderquery.domain.orderquery.dto.message.OrderStatusUpdateEvent;
import com.example.orderquery.domain.orderquery.entity.OrderQueryModel;
import com.example.orderquery.domain.orderquery.repository.OrderQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderQueryService {

    private final OrderQueryRepository orderQueryRepository;

    public void saveOrderQuery(OrderCreatedEvent event) {
        // Query용 데이터 저장
        OrderQueryModel queryModel = new OrderQueryModel(
            event.getOrderId(),
            event.getOrderStatus(),
            event.getOrderTime(),
            event.getProductId(),
            event.getQuantity(),
            event.getProductName(),
            event.getProductPrice()
        );

        orderQueryRepository.save(queryModel);
    }

    public OrderHistoryResponse findOrderList() {
        List<OrderQueryModel> orderQueryModelList = orderQueryRepository.findAll();

        List<OrderResponse> orderResponseList = orderQueryModelList.stream()
            .map(OrderResponse::from)
            .toList();

        return new OrderHistoryResponse(orderResponseList);
    }

    public void updateOrderQueryStatus(OrderStatusUpdateEvent event) {
        OrderQueryModel orderQueryModel = orderQueryRepository.findByOrderId(event.getOrderId())
            .orElseThrow(() -> new RuntimeException("Order Not Found"));

        orderQueryModel.updateStatus(event.getOrderStatus());

        // mongoDB는 변경감지 X
        orderQueryRepository.save(orderQueryModel);
    }
}
