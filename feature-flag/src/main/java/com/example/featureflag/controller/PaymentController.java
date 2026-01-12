package com.example.featureflag.controller;

import com.example.featureflag.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 처리 API
     * Feature Flag 'use-new-payment-engine'이 활성화되면 신규 결제 엔진 사용,
     * 비활성화되면 PaymentFallbackService의 fallback 메서드 실행
     */
    @PostMapping("/process")
    public ResponseEntity<String> processPayment(
            @RequestParam String orderId,
            @RequestParam int amount
    ) {
        log.info("결제 요청 - orderId: {}, amount: {}", orderId, amount);
        return ResponseEntity.ok(paymentService.processPayment(orderId, amount));
    }
}
