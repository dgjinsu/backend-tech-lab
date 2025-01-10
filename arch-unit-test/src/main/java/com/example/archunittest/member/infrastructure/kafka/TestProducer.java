package com.example.archunittest.member.infrastructure.kafka;

import com.example.archunittest.member.application.spec.KafkaSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestProducer implements KafkaSpec {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC_NAME = "test-topic";

    @Override
    public void sendTest(String message) {
        try {
            kafkaTemplate.send(TOPIC_NAME, message);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
