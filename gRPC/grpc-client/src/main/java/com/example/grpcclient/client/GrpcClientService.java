package com.example.grpcclient.client;

import com.example.grpc.proto.SampleRequest;
import com.example.grpc.proto.SampleResponse;
import com.example.grpc.proto.SampleServiceGrpc;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GrpcClientService {

    @GrpcClient("local-grpc-server")
    private SampleServiceGrpc.SampleServiceBlockingStub sampleServiceBlockingStub;

    private final SampleClient sampleClient;

    public String sendMessage(String userId, String message) {
        SampleRequest request = SampleRequest.newBuilder()
            .setUserId(userId)
            .setMessage(message)
            .build();

        SampleResponse response = sampleServiceBlockingStub.sampleCall(request);

        log.info("[gRPC client] message: {}", response.getMessage());

        return response.getMessage();
    }

    public String sendMessageWithHttp(String userId, String message) {
        // SampleRequest 객체 생성
        SampleRequestWithHttp request = new SampleRequestWithHttp(userId, message);

        // Feign Client로 요청 전송
        SampleResponseWithHttp response = sampleClient.sampleCall(request);

        log.info("[HTTP client] message: {}", response.getMessage());

        return response.getMessage();
    }

    @AllArgsConstructor
    @Getter
    public static class SampleRequestWithHttp {

        private String userId;
        private String message;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class SampleResponseWithHttp {

        private String message;
    }

}
