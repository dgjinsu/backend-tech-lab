package com.example.featureflag.global.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "feature_flags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeatureFlagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String featureKey;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String targetRoles;

    public FeatureFlagEntity(String featureKey, Boolean enabled, String description, String targetRoles) {
        this.featureKey = featureKey;
        this.enabled = enabled;
        this.description = description;
        this.targetRoles = targetRoles;
    }

    public void updateEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 해당 role이 feature flag 적용 대상인지 확인
     * - targetRoles가 비어있으면 모든 role에 적용
     * - targetRoles에 해당 role이 있으면 적용
     */
    public boolean isTargetRole(String role) {
        if (targetRoles == null || targetRoles.isBlank()) {
            return true;
        }
        Set<String> roles = Arrays.stream(targetRoles.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
        return roles.contains(role);
    }
}
