package com.budget.api.domain.category.entity;

import com.budget.api.domain.user.entity.User;
import com.budget.api.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    private Category(String name, CategoryType type, User user) {
        this.name = name;
        this.type = type;
        this.user = user;
    }

    public static Category createDefault(String name) {
        return Category.builder()
                .name(name)
                .type(CategoryType.DEFAULT)
                .user(null)
                .build();
    }

    public static Category createCustom(String name, User user) {
        return Category.builder()
                .name(name)
                .type(CategoryType.CUSTOM)
                .user(user)
                .build();
    }
}
