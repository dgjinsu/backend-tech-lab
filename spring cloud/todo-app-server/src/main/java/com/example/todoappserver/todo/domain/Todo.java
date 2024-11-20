package com.example.todoappserver.todo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Todo {

    private Long id;
    private String content;
    private Boolean isCompleted;
    private Long memberId;


    public void update(String newContent, Boolean isComplete) {
        if (newContent != null) {
            this.content = newContent;
        }
        if (isComplete != null) {
            this.isCompleted = isComplete;
        }
    }
}