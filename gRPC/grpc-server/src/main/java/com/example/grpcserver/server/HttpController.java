package com.example.grpcserver.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HttpController {

    @PostMapping("/sample")
    public SampleResponse sampleCall(@RequestBody SampleRequest request) {
        String userId = request.getUserId();
        String message = request.getMessage();

        // 비즈니스 로직 처리
        String responseMessage =
            "Received message from userId: " + userId + ", message: " + message;

        log.info("[HTTP server] message: {}", responseMessage);

        // Response 생성 및 반환
        return new SampleResponse(responseMessage);
    }

    @NoArgsConstructor
    @Getter
    public static class SampleRequest {

        private String userId;
        private String message;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class SampleResponse {

        private String message;
    }
}
