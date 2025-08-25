package com.example.featureflag.controller;

import com.example.featureflag.config.annotation.FeatureFlag;
import com.example.featureflag.dto.ApiResponse;
import com.example.featureflag.dto.FeatureTestResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class FeatureTestController {

    // 1. test1 피쳐 테스트 (피쳐가 꺼져있으면 예외 발생)
    @FeatureFlag(value = "test1")
    @GetMapping("/test1")
    public ApiResponse<FeatureTestResponse> test1() {
        FeatureTestResponse response = FeatureTestResponse.builder()
                .message("🎉 test1 기능이 활성화되었습니다!")
                .status("enabled")
                .feature("test1")
                .build();
        return ApiResponse.success(response);
    }

    // 2. test2 피쳐 테스트 (피쳐가 켜져있으면 정상 동작)
    @FeatureFlag(value = "test2")
    @GetMapping("/test2")
    public ApiResponse<FeatureTestResponse> test2() {
        FeatureTestResponse response = FeatureTestResponse.builder()
                .message("🚀 test2 기능이 활성화되었습니다!")
                .status("enabled")
                .feature("test2")
                .build();
        return ApiResponse.success(response);
    }

    // 3. test3 피쳐 테스트 (피쳐가 꺼져있으면 예외 발생)
    @FeatureFlag(value = "test3")
    @GetMapping("/test3")
    public ApiResponse<FeatureTestResponse> test3() {
        FeatureTestResponse response = FeatureTestResponse.builder()
                .message("🔬 test3 기능이 활성화되었습니다!")
                .status("enabled")
                .feature("test3")
                .build();
        return ApiResponse.success(response);
    }
}
