package com.example.featureflag.global.service;

import com.example.featureflag.global.domain.FeatureFlagEntity;
import com.example.featureflag.global.repository.FeatureFlagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeatureFlagService {

    private final FeatureFlagRepository featureFlagRepository;

    /**
     * Feature flag가 활성화되어 있는지 확인
     */
    public boolean isEnabled(String featureKey) {
        return featureFlagRepository.findByFeatureKey(featureKey)
                .map(FeatureFlagEntity::getEnabled)
                .orElseGet(() -> {
                    log.warn("Feature flag not found: {}. Returning false as default.", featureKey);
                    return false;
                });
    }

    /**
     * Feature flag 상태 변경
     */
    @Transactional
    public void updateFeatureFlag(String featureKey, boolean enabled) {
        FeatureFlagEntity featureFlag = featureFlagRepository.findByFeatureKey(featureKey)
                .orElseThrow(() -> new IllegalArgumentException("Feature flag not found: " + featureKey));

        featureFlag.updateEnabled(enabled);
        log.info("Feature flag updated: {} -> {}", featureKey, enabled);
    }

    /**
     * 새로운 Feature flag 생성
     */
    @Transactional
    public FeatureFlagEntity createFeatureFlag(String featureKey, boolean enabled, String description) {
        FeatureFlagEntity featureFlag = new FeatureFlagEntity(featureKey, enabled, description);
        return featureFlagRepository.save(featureFlag);
    }
}
