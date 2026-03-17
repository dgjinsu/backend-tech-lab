package com.example.rabbitmqparallel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskMessage {
    private String messageId;
    private int sequenceNumber;
    private String payload;
    private long publishedAt;
}
