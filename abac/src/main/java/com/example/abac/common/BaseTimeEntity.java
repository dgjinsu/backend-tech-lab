package com.example.abac.common;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// @MappedSuperclass: 테이블은 안 만들고 자식 엔티티의 컬럼으로 필드를 내려준다.
// AuditingEntityListener가 persist/update 훅을 잡아서 생성/수정 시각을 자동 기록.
// 단, AbacApplication의 @EnableJpaAuditing이 없으면 이 리스너가 꺼진 상태라 시각이 null로 남는다.
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
