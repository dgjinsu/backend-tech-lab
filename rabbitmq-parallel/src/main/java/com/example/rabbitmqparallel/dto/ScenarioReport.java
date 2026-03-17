package com.example.rabbitmqparallel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioReport {
    private String scenarioName;
    private int concurrency;
    private int totalMessages;
    private int successCount;
    private int failCount;
    private long totalElapsedMs;
    private double throughputPerMinute;
    private double avgProcessingTimeMs;
    private double p50ProcessingTimeMs;
    private double p95ProcessingTimeMs;
    private double p99ProcessingTimeMs;
    private double avgQueueLatencyMs;
    private int peakConcurrentProcessing;
    private double peakMemoryMb;
    private int peakThreadCount;
    private String reportGeneratedAt;
}
