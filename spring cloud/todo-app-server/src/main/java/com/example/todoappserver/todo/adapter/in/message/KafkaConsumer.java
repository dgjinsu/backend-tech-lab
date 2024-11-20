package com.example.todoappserver.todo.adapter.in.message;

import com.example.todoappserver.todo.application.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final ObjectMapper mapper = new ObjectMapper();
    private final TodoService todoService;

    /**
     * Temp 저장 실패 시 보상 트랜잭션을 실행
     */
    @KafkaListener(topics = "temp-save-failed-topic", groupId = "todo-compensation-group")
    public void handleTempSaveFailure(String message) {
        try {
            // 실패한 Todo ID를 받아와서 보상 트랜잭션 실행
            Long todoId = Long.valueOf(message);
            todoService.deleteTodoById(todoId);

            log.info("todo service: 보상 트랜잭션 실행, todoId = {}", todoId);
        } catch (Exception e) {
            log.error("Error handling compensation transaction for Todo", e);
        }
    }
}