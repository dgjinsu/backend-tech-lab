package com.budget.api.domain.department.entity;

import com.budget.api.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "departments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Builder
    private Department(String name) {
        this.name = name;
    }

    public static Department create(String name) {
        return Department.builder()
                .name(name)
                .build();
    }
}
