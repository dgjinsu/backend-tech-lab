package com.example.featureflag.repository;

import com.example.featureflag.entity.FeatureFlagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeatureFlagRepository extends JpaRepository<FeatureFlagEntity, Long> {
    
    /**
     * 피쳐 이름으로 피쳐 플래그 조회
     */
    Optional<FeatureFlagEntity> findByFeatureName(String featureName);
    
    /**
     * 피쳐 이름으로 활성화 여부만 조회
     */
    @Query("SELECT f.enabled FROM FeatureFlagEntity f WHERE f.featureName = :featureName")
    Optional<Boolean> findEnabledByFeatureName(@Param("featureName") String featureName);
}
