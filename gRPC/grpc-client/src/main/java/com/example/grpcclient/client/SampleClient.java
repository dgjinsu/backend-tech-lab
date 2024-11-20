package com.example.grpcclient.client;

import com.example.grpc.proto.SampleResponse;
import com.example.grpcclient.client.GrpcClientService.SampleRequestWithHttp;
import com.example.grpcclient.client.GrpcClientService.SampleResponseWithHttp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sample-service", url = "http://localhost:8081/") // HTTP 서버 URL
public interface SampleClient {
    @PostMapping("/sample")
    SampleResponseWithHttp sampleCall(@RequestBody SampleRequestWithHttp request);
}
