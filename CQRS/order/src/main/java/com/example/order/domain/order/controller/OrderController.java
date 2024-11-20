package com.example.order.domain.order.controller;

import com.example.order.domain.order.dto.OrderHistoryResponse;
import com.example.order.domain.order.dto.OrderSaveRequest;
import com.example.order.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("")
    public ResponseEntity<String> saveOrder(@RequestBody OrderSaveRequest request) {
        orderService.saveOrder(request);
        return ResponseEntity.ok("저장 완료");
    }

    @GetMapping("")
    public ResponseEntity<OrderHistoryResponse> saveOrder() {
        return ResponseEntity.ok(orderService.findOrderList());
    }
}
