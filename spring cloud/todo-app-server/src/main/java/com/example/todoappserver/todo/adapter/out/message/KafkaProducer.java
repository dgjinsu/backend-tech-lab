package com.example.todoappserver.todo.adapter.out.message;

import brave.Tracer;
import com.example.todoappserver.todo.adapter.in.web.dto.TodoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Tracer tracer; // Sleuth의 Tracer를 사용해 현재 trace 정보를 가져옴

    private final ObjectMapper mapper = new ObjectMapper(); // ObjectMapper를 필드로 선언하여 재사용

    public void send(String topic, TodoResponse response) {
        String traceId = tracer.currentSpan().context().traceIdString();
        String spanId = tracer.currentSpan().context().spanIdString();

        // Kafka 메시지에 traceId와 spanId를 헤더로 추가
        Message<String> message = null;
        try {
            message = MessageBuilder
                    .withPayload(mapper.writeValueAsString(response))
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader("X-B3-TraceId", traceId)
                    .setHeader("X-B3-SpanId", spanId)
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        kafkaTemplate.send(message);

        log.info("[todo] kafka send complete");
    }
}