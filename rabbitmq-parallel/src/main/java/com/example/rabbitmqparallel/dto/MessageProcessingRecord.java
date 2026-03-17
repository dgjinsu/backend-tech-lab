package com.example.rabbitmqparallel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageProcessingRecord {
    private String messageId;
    private int sequenceNumber;
    private long publishedAt;
    private long receivedAt;
    private long processingStartAt;
    private long processingEndAt;
    private String consumerThread;
    private int parallelCallCount;
    private List<SubCallRecord> subCallRecords;
    private boolean success;
    private String errorMessage;

    public long getQueueLatencyMs() {
        return receivedAt - publishedAt;
    }

    public long getProcessingTimeMs() {
        return processingEndAt - processingStartAt;
    }

    public long getTotalLatencyMs() {
        return processingEndAt - publishedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubCallRecord {
        private String callName;
        private long startAt;
        private long endAt;
        private long durationMs;
        private boolean success;
    }
}
