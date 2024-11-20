package com.example.tempserver.domain.temp.message;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import com.example.tempserver.domain.temp.message.dto.TodoSaveRequestDto;
import com.example.tempserver.domain.temp.service.TempService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final TempService tempService;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Tracer tracer; // Sleuth Tracer를 사용하여 트레이싱 정보 설정

    @KafkaListener(topics = KafkaConstants.TODO_SAVE_TOPIC, groupId = "temp-group", containerFactory = "kafkaListenerContainerFactory")
    public void saveTemp(Message<String> message) {
        TodoSaveRequestDto request = null;
        try {
            // 메시지 헤더에서 traceId와 spanId 추출
            MessageHeaders headers = message.getHeaders();
            String traceIdHex = (String) headers.get("X-B3-TraceId");
            String spanIdHex = (String) headers.get("X-B3-SpanId");


            if (traceIdHex != null && spanIdHex != null) {
                // 128비트 traceId일 경우 상위 64비트와 하위 64비트 분리
                long traceIdHigh = traceIdHex.length() > 16 ? Long.parseUnsignedLong(traceIdHex.substring(0, traceIdHex.length() - 16), 16) : 0;
                long traceIdLow = Long.parseUnsignedLong(traceIdHex.substring(traceIdHex.length() - 16), 16);
                long spanId = Long.parseUnsignedLong(spanIdHex, 16);

                // TraceContext 생성
                TraceContext traceContext = TraceContext.newBuilder()
                        .traceIdHigh(traceIdHigh)
                        .traceId(traceIdLow)
                        .spanId(spanId)
                        .sampled(true)
                        .build();

                // Tracer를 사용해 Span 생성 및 활성화
                Span newSpan = tracer.joinSpan(traceContext);
                try (Tracer.SpanInScope ws = tracer.withSpanInScope(newSpan.start())) {
                    // 실제 로직 수행
                    request = objectMapper.readValue(message.getPayload(), TodoSaveRequestDto.class);
                    tempService.saveTemp(request);

                    log.info("temp consumer processed request: {}", request);
                } finally {
                    newSpan.finish(); // Span 종료
                }
            } else {
                log.warn("TraceId or SpanId is missing in the headers");
            }
        } catch (Exception e) {
            log.error("Error processing message", e);

            if (request != null) {
                sendCompensationEvent(request.todoId());
            }
        }
    }

    private void sendCompensationEvent(Long todoId) {
        try {
            kafkaTemplate.send("temp-save-failed-topic", todoId.toString());
            log.info("Compensation event sent for Todo ID {}", todoId);
        } catch (Exception e) {
            log.error("Error sending compensation event", e);
        }
    }
}