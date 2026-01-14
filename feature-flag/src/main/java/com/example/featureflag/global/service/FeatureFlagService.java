package com.example.featureflag.global.service;

import com.example.featureflag.global.domain.FeatureFlagEntity;
import com.example.featureflag.global.repository.FeatureFlagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

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
     * 주어진 role이 feature flag 적용 대상인지 확인
     * (flag 활성화 여부와 무관하게 role만 체크)
     */
    public boolean isTargetRole(String featureKey, String role) {
        return featureFlagRepository.findByFeatureKey(featureKey)
                .map(flag -> flag.isTargetRole(role))
                .orElse(false);
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
     * 새로운 Feature flag 생성 (적용 대상 권한 포함)
     */
    @Transactional
    public FeatureFlagEntity createFeatureFlag(String featureKey, boolean enabled, String description, Set<String> roles) {
        String targetRoles = (roles != null && !roles.isEmpty()) ? String.join(",", roles) : null;
        FeatureFlagEntity featureFlag = new FeatureFlagEntity(featureKey, enabled, description, targetRoles);
        return featureFlagRepository.save(featureFlag);
    }
}
