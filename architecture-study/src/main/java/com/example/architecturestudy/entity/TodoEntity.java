package com.example.architecturestudy.entity;

import com.example.architecturestudy.entity.common.BaseEntity;
import com.example.architecturestudy.entity.enums.TodoStatus;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodoStatus status;

    @Builder
    public TodoEntity(String title, String content) {
        this.title = title;
        this.content = content;
        this.status = TodoStatus.PENDING;
    }

    public void updateTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void updateStatus(TodoStatus status) {
        this.status = status;
    }
}
