package com.example.orderquery.domain.orderquery.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    CANCEL("취소"),
    COMPLETE("완료");

    private final String description;
}
