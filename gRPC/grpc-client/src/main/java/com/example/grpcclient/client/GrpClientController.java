package com.example.grpcclient.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GrpClientController {
    private final GrpcClientService grpcClientService;

    @GetMapping("/grp/test")
    public ResponseEntity<String> test() {
        String message = grpcClientService.sendMessage("userId1", "message1");
        return ResponseEntity.ok(message);
    }

    @GetMapping("/http/test")
    public ResponseEntity<String> httpTest() {
        String message = grpcClientService.sendMessageWithHttp("userId1", "message1");
        return ResponseEntity.ok(message);
    }

}
