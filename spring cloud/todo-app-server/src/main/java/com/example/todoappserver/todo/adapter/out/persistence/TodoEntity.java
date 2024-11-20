package com.example.todoappserver.todo.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TodoEntity {

    @Id
    @GeneratedValue
    @Column(name = "todo_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean isCompleted;

    @Column(name = "member_id")
    private Long memberId;

    @Builder
    public TodoEntity(String content, Boolean isCompleted, Long memberId) {
        this.content = content;
        this.isCompleted = isCompleted;
        this.memberId = memberId;
    }
}