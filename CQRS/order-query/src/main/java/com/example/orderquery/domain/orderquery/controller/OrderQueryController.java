package com.example.orderquery.domain.orderquery.controller;

import com.example.orderquery.domain.orderquery.dto.feign.OrderHistoryResponse;
import com.example.orderquery.domain.orderquery.service.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order-query")
public class OrderQueryController {

    private final OrderQueryService orderQueryService;

    @GetMapping("")
    public ResponseEntity<OrderHistoryResponse> getOrderHistory() {
        return ResponseEntity.ok(orderQueryService.findOrderList());
    }
}
