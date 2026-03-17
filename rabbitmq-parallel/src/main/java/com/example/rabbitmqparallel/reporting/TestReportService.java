package com.example.rabbitmqparallel.reporting;

import com.example.rabbitmqparallel.config.ConsumerProperties;
import com.example.rabbitmqparallel.dto.MessageProcessingRecord;
import com.example.rabbitmqparallel.dto.ScenarioReport;
import com.example.rabbitmqparallel.tracking.ProcessingTracker;
import com.example.rabbitmqparallel.tracking.ResourceMonitor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestReportService {

    private final ProcessingTracker tracker;
    private final ResourceMonitor resourceMonitor;
    private final ConsumerProperties consumerProperties;
    private final ObjectMapper objectMapper;

    public ScenarioReport generateReport(String scenarioName, int totalMessages) {
        Map<String, MessageProcessingRecord> records = tracker.getAllRecords();

        long successCount = records.values().stream()
                .filter(MessageProcessingRecord::isSuccess).count();
        long failCount = records.values().stream()
                .filter(r -> r.getProcessingEndAt() > 0 && !r.isSuccess()).count();

        List<Long> processingTimes = records.values().stream()
                .filter(r -> r.getProcessingEndAt() > 0)
                .map(MessageProcessingRecord::getProcessingTimeMs)
                .sorted()
                .toList();

        double avgProcessingTime = processingTimes.stream()
                .mapToLong(Long::longValue).average().orElse(0);

        double avgQueueLatency = records.values().stream()
                .filter(r -> r.getReceivedAt() > 0)
                .mapToLong(MessageProcessingRecord::getQueueLatencyMs)
                .average().orElse(0);

        long earliestPublish = records.values().stream()
                .mapToLong(MessageProcessingRecord::getPublishedAt).min().orElse(0);
        long latestEnd = records.values().stream()
                .mapToLong(MessageProcessingRecord::getProcessingEndAt).max().orElse(0);
        long totalElapsed = latestEnd - earliestPublish;

        double throughput = totalElapsed > 0 ? successCount * 60_000.0 / totalElapsed : 0;

        return ScenarioReport.builder()
                .scenarioName(scenarioName)
                .concurrency(consumerProperties.getConcurrency())
                .totalMessages(totalMessages)
                .successCount((int) successCount)
                .failCount((int) failCount)
                .totalElapsedMs(totalElapsed)
                .throughputPerMinute(Math.round(throughput * 100.0) / 100.0)
                .avgProcessingTimeMs(Math.round(avgProcessingTime))
                .p50ProcessingTimeMs(percentile(processingTimes, 0.5))
                .p95ProcessingTimeMs(percentile(processingTimes, 0.95))
                .p99ProcessingTimeMs(percentile(processingTimes, 0.99))
                .avgQueueLatencyMs(Math.round(avgQueueLatency))
                .peakConcurrentProcessing(tracker.getPeakConcurrent())
                .peakMemoryMb(Math.round(resourceMonitor.getPeakMemoryMb() * 10.0) / 10.0)
                .peakThreadCount(resourceMonitor.getPeakThreadCount())
                .reportGeneratedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }

    public void exportToFile(ScenarioReport report) throws IOException {
        String filename = "report-" + report.getScenarioName() + "-"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
                + ".json";
        Path path = Path.of("reports", filename);
        Files.createDirectories(path.getParent());
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(path.toFile(), report);
        log.info("Report exported to {}", path.toAbsolutePath());
    }

    private double percentile(List<Long> sorted, double p) {
        if (sorted.isEmpty()) return 0;
        int index = (int) Math.ceil(p * sorted.size()) - 1;
        return sorted.get(Math.max(0, index));
    }
}
