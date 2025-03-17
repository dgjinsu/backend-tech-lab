package com.example.jaegertest;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/service")
//@RequiredArgsConstructor
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    private final RestTemplate restTemplate;
    private final TestRepository testRepository;

    public TestController(RestTemplate restTemplate, TestRepository testRepository) {
        this.restTemplate = restTemplate;
        this.testRepository = testRepository;
    }

    @Value("${spring.application.name}")
    private String applicationName;

    @GetMapping("/path1")
    public ResponseEntity<String> path1() {
        logger.info("Incoming request at {} for request /path1", applicationName);
        String response = restTemplate.getForObject("http://localhost:8090/service/path2", String.class);
        return ResponseEntity.ok("response from /path1 " + response);
    }

    @GetMapping("/path2")
    public ResponseEntity<String> path2() {
        logger.info("Incoming request at {} at /path2", applicationName);
        return ResponseEntity.ok("response from /path2 ");
    }

    @GetMapping("/path3")
    public ResponseEntity<String> path3() {
        testRepository.save(new Test("test"));
        logger.info("Incoming request at {} at /path2", applicationName);
        return ResponseEntity.ok("response from /path2 ");
    }
}