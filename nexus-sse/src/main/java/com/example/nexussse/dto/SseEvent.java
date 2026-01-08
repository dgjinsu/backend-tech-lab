package com.example.nexussse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SseEvent {

    private String id;
    private String name;
    private Object data;

    public static SseEvent of(String name, Object data) {
        return SseEvent.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .data(data)
                .build();
    }
}
