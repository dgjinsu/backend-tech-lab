package com.example.featureflag.controller;

import com.example.featureflag.config.annotation.FeatureFlag;
import com.example.featureflag.dto.ApiResponse;
import com.example.featureflag.dto.FeatureFlagResponse;
import com.example.featureflag.dto.FeatureTestResponse;
import com.example.featureflag.dto.FeatureToggleResponse;
import com.example.featureflag.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class FeatureController {

    private final FeatureFlagService featureFlagService;

    // 메인 피쳐 플래그 관리 화면
    @GetMapping("/")
    public String featureFlagManagement(Model model) {
        Map<String, Boolean> allFlags = featureFlagService.getAllFeatureFlags();
        model.addAttribute("featureFlags", allFlags);
        return "feature-flags";
    }

    // 1. 모든 피쳐 플래그 상태 조회 (API)
    @GetMapping("/api/features")
    @ResponseBody
    public ApiResponse<FeatureFlagResponse> getAllFeatureFlags() {
        Map<String, Boolean> allFlags = featureFlagService.getAllFeatureFlags();
        
        FeatureFlagResponse response = FeatureFlagResponse.builder()
                .totalFeatures(allFlags.size())
                .enabledFeatures(allFlags.entrySet().stream()
                        .filter(Map.Entry::getValue)
                        .map(Map.Entry::getKey)
                        .toList())
                .disabledFeatures(allFlags.entrySet().stream()
                        .filter(entry -> !entry.getValue())
                        .map(Map.Entry::getKey)
                        .toList())
                .allFlags(allFlags)
                .build();
        
        return ApiResponse.success(response, "피쳐 플래그 목록을 성공적으로 조회했습니다.");
    }

    // 2. 피쳐 플래그 상태 토글 (API)
    @PostMapping("/api/feature/{flagName}/toggle")
    @ResponseBody
    public ApiResponse<FeatureToggleResponse> toggleFeatureFlag(@PathVariable String flagName) {
        try {
            boolean newState = featureFlagService.toggleFeatureFlag(flagName);
            
            FeatureToggleResponse response = FeatureToggleResponse.builder()
                    .flagName(flagName)
                    .newState(newState)
                    .message("피쳐 플래그가 성공적으로 토글되었습니다")
                    .build();
            
            return ApiResponse.success(response, "피쳐 플래그가 성공적으로 토글되었습니다");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // 3. test1 피쳐 테스트 (피쳐가 꺼져있으면 예외 발생)
    @FeatureFlag(value = "test1")
    @GetMapping("/test1")
    @ResponseBody
    public ApiResponse<FeatureTestResponse> test1() {
        FeatureTestResponse response = FeatureTestResponse.builder()
                .message("🎉 test1 기능이 활성화되었습니다!")
                .status("enabled")
                .feature("test1")
                .build();
        return ApiResponse.success(response);
    }

    // 4. test2 피쳐 테스트 (피쳐가 켜져있으면 정상 동작)
    @FeatureFlag(value = "test2")
    @GetMapping("/test2")
    @ResponseBody
    public ApiResponse<FeatureTestResponse> test2() {
        FeatureTestResponse response = FeatureTestResponse.builder()
                .message("🚀 test2 기능이 활성화되었습니다!")
                .status("enabled")
                .feature("test2")
                .build();
        return ApiResponse.success(response);
    }

    // 5. test3 피쳐 테스트 (피쳐가 꺼져있으면 예외 발생)
    @FeatureFlag(value = "test3")
    @GetMapping("/test3")
    @ResponseBody
    public ApiResponse<FeatureTestResponse> test3() {
        FeatureTestResponse response = FeatureTestResponse.builder()
                .message("🔬 test3 기능이 활성화되었습니다!")
                .status("enabled")
                .feature("test3")
                .build();
        return ApiResponse.success(response);
    }
}