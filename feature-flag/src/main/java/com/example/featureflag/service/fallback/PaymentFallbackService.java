package com.example.featureflag.service.fallback;

import com.example.featureflag.global.aop.annotation.FeatureFallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 결제 Fallback 서비스
 */
@Service
@Slf4j
public class PaymentFallbackService {

    /**
     * 결제 처리 Fallback
     */
    @FeatureFallback(key = "use-new-payment-engine")
    public String processPaymentFallback(String orderId, int amount) {
        log.info("📦 [기존 결제 엔진] Fallback 결제 처리 시작");

        String result = String.format("✅ [기존 엔진] 결제 완료 - 주문: %s, 금액: %,d원", orderId, amount);
        log.info(result);

        return result;
    }
}

