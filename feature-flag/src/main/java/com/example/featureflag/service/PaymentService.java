package com.example.featureflag.service;

import com.example.featureflag.global.aop.annotation.FeatureFlag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 결제 서비스
 */
@Slf4j
@Service
public class PaymentService {

    /**
     * 결제 처리
     * Feature Flag 'use-new-payment-engine'이 활성화되면 이 메서드가 실행되고,
     * 비활성화되면 PaymentFallbackService.processPaymentFallback()이 실행.
     */
    @FeatureFlag(key = "use-new-payment-engine")
    public String processPayment(String orderId, int amount) {
        log.info("🆕 [신규 결제 엔진] 결제 처리 시작");

        String result = String.format("✅ [신규 엔진] 결제 완료 - 주문: %s, 금액: %,d원", orderId, amount);
        log.info(result);

        return result;
    }
}

