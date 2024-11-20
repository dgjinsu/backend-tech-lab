package com.example.tempserver.domain.temp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Temp {

    @Id
    @GeneratedValue
    @Column(name = "temp_id")
    private Long id;

    private String content;
    private LocalDateTime createdAt;

    @Column(name = "todo_id")
    private Long todoId;

    @Builder
    public Temp(String content, LocalDateTime createdAt, Long todoId) {
        this.content = content;
        this.createdAt = createdAt;
        this.todoId = todoId;
    }
}
