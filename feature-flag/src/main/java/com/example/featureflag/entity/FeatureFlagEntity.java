package com.example.featureflag.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feature_flags")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlagEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "feature_name", unique = true, nullable = false, length = 100)
    private String featureName;
    
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;
    
    @Column(name = "description", length = 500)
    private String description;
}
