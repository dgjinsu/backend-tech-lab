package com.example.featureflag.controller;

import com.example.featureflag.global.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feature-flags")
@RequiredArgsConstructor
public class FeatureFlagController {

    private final FeatureFlagService featureFlagService;

    /**
     * Feature flag 상태 확인
     */
    @GetMapping("/{featureKey}")
    public boolean checkFeatureFlag(@PathVariable String featureKey) {
        return featureFlagService.isEnabled(featureKey);
    }

    /**
     * Feature flag 활성화/비활성화
     */
    @PutMapping("/{featureKey}")
    public String updateFeatureFlag(
            @PathVariable String featureKey,
            @RequestParam boolean enabled) {
        featureFlagService.updateFeatureFlag(featureKey, enabled);
        return String.format("Feature '%s' is now %s", featureKey, enabled ? "enabled" : "disabled");
    }
}
