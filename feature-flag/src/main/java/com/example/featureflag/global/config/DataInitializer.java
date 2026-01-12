package com.example.featureflag.global.config;

import com.example.featureflag.global.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 시작 시 초기 Feature Flag 데이터를 생성합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final FeatureFlagService featureFlagService;

    @Override
    public void run(String... args) {
        featureFlagService.createFeatureFlag(
                "use-new-payment-engine",
                true,
                "신규 결제 엔진 사용 여부"
        );

        log.info("use-new-payment-engine = true 에 대한 피쳐플래그 데이터 INSERT");
    }
}
