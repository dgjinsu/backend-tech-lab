package com.example.grpcserver.server;

import com.example.grpc.proto.SampleRequest;
import com.example.grpc.proto.SampleResponse;
import com.example.grpc.proto.SampleServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
public class GrpcServer extends SampleServiceGrpc.SampleServiceImplBase {

    @Override
    public void sampleCall(SampleRequest request, StreamObserver<SampleResponse> responseObserver) {
        String userId = request.getUserId();
        String message = request.getMessage();

        // 비즈니스 로직 처리
        String responseMessage = "Received message from userId: " + userId + ", message: " + message;

        log.info("[gRPC server] message: {}", responseMessage);


        // Response 생성
        SampleResponse response = SampleResponse.newBuilder()
            .setMessage(responseMessage)
            .build();

        // 응답 반환
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
