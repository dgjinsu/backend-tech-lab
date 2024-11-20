package com.example.orderquery.domain.orderquery.service;

import com.example.orderquery.domain.orderquery.dto.feign.OrderHistoryResponse;
import com.example.orderquery.domain.orderquery.dto.feign.OrderResponse;
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

    public OrderHistoryResponse findOrderList() {
        List<OrderQueryModel> orderQueryModelList = orderQueryRepository.findAll();

        List<OrderResponse> orderResponseList = orderQueryModelList.stream()
            .map(OrderResponse::from)
            .toList();

        return new OrderHistoryResponse(orderResponseList);
    }
}
