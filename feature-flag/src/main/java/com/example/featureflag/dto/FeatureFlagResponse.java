package com.example.featureflag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlagResponse {
    private int totalFeatures;
    private List<String> enabledFeatures;
    private List<String> disabledFeatures;
    private Map<String, Boolean> allFlags;
}
