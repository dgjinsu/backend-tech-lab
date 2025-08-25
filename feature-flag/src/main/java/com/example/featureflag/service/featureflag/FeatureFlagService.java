package com.example.featureflag.service.featureflag;

import com.example.featureflag.entity.FeatureFlagEntity;
import com.example.featureflag.repository.FeatureFlagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeatureFlagService implements CommandLineRunner {
    
    private final FeatureFlagRepository featureFlagRepository;
    
    /**
     * 애플리케이션 시작 시 기본 피쳐 플래그들을 데이터베이스에 초기화
     */
    @Override
    public void run(String... args) throws Exception {
        initializeDefaultFeatureFlags();
        log.info("피쳐 플래그 서비스가 초기화되었습니다.");
    }
    
    /**
     * 기본 피쳐 플래그들을 데이터베이스에 초기화
     */
    @Transactional
    public void initializeDefaultFeatureFlags() {
        if (featureFlagRepository.count() == 0) {
            log.info("기본 피쳐 플래그들을 초기화합니다.");
            
            List<FeatureFlagEntity> defaultFlags = List.of(
                createFeatureFlag("test1", false, "테스트 기능 1"),
                createFeatureFlag("test2", true, "테스트 기능 2"),
                createFeatureFlag("test3", false, "테스트 기능 3")
            );
            
            featureFlagRepository.saveAll(defaultFlags);
            log.info("{}개의 기본 피쳐 플래그가 데이터베이스에 저장되었습니다.", defaultFlags.size());
        }
    }
    
    /**
     * 피쳐 플래그 엔티티 생성 헬퍼 메서드
     */
    private FeatureFlagEntity createFeatureFlag(String name, boolean enabled, String description) {
        return FeatureFlagEntity.builder()
                .featureName(name)
                .enabled(enabled)
                .description(description)
                .build();
    }
    
    /**
     * 특정 피쳐 플래그가 활성화되어 있는지 확인
     */
    @Transactional(readOnly = true)
    public boolean isFeatureEnabled(String featureName) {
        Optional<Boolean> dbResult = featureFlagRepository.findEnabledByFeatureName(featureName);
        if (dbResult.isPresent()) {
            return dbResult.get();
        }
        
        // 존재하지 않는 피쳐는 기본적으로 비활성화
        log.warn("존재하지 않는 피쳐 플래그: {}", featureName);
        return false;
    }
    
    /**
     * 피쳐 플래그 상태 토글
     */
    @Transactional
    public boolean toggleFeatureFlag(String featureName) {
        FeatureFlagEntity entity = featureFlagRepository.findByFeatureName(featureName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 피쳐 플래그: " + featureName));
        
        boolean newState = !entity.getEnabled();
        entity.setEnabled(newState);
        featureFlagRepository.save(entity);
        
        log.info("피쳐 플래그 '{}' 상태가 {}로 토글되었습니다.", featureName, newState);
        return newState;
    }
    
    /**
     * 모든 피쳐 플래그 상태 조회
     */
    @Transactional(readOnly = true)
    public Map<String, Boolean> getAllFeatureFlags() {
        List<FeatureFlagEntity> allFlags = featureFlagRepository.findAll();
        return allFlags.stream()
                .collect(Collectors.toMap(
                    FeatureFlagEntity::getFeatureName,
                    FeatureFlagEntity::getEnabled
                ));
    }
}
