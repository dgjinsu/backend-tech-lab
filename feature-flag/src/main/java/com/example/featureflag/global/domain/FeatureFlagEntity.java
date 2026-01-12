package com.example.featureflag.global.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public FeatureFlagEntity(String featureKey, Boolean enabled, String description) {
        this.featureKey = featureKey;
        this.enabled = enabled;
        this.description = description;
    }

    public void updateEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
