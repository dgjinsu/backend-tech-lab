package com.example.rabbitmqparallel.controller;

import com.example.rabbitmqparallel.config.ConsumerProperties;
import com.example.rabbitmqparallel.dto.ScenarioReport;

import com.example.rabbitmqparallel.producer.MessageProducer;
import com.example.rabbitmqparallel.reporting.TestReportService;
import com.example.rabbitmqparallel.tracking.ProcessingTracker;
import com.example.rabbitmqparallel.tracking.ResourceMonitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final MessageProducer producer;
    private final ProcessingTracker tracker;
    private final ResourceMonitor resourceMonitor;
    private final TestReportService reportService;
    private final ConsumerProperties consumerProperties;

    @PostMapping("/publish")
    public ResponseEntity<Map<String, Object>> publish(
            @RequestParam(defaultValue = "200") int count) {
        tracker.reset();
        resourceMonitor.startMonitoring();

        producer.publishBurst(count);

        return ResponseEntity.ok(Map.of(
                "published", count,
                "config", Map.of(
                        "concurrency", consumerProperties.getConcurrency(),
                        "maxConcurrency", consumerProperties.getMaxConcurrency()
                )
        ));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status(
            @RequestParam(defaultValue = "200") int expected) {
        return ResponseEntity.ok(Map.of(
                "expected", expected,
                "received", tracker.getTotalReceived(),
                "processed", tracker.getTotalProcessed(),
                "currentConcurrent", tracker.getCurrentConcurrent(),
                "peakConcurrent", tracker.getPeakConcurrent(),
                "complete", tracker.isComplete(expected)
        ));
    }

    @GetMapping("/report")
    public ResponseEntity<ScenarioReport> report(
            @RequestParam(defaultValue = "default") String name,
            @RequestParam(defaultValue = "200") int total) throws IOException {
        resourceMonitor.stopMonitoring();
        ScenarioReport report = reportService.generateReport(name, total);
        reportService.exportToFile(report);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> reset() {
        tracker.reset();
        resourceMonitor.stopMonitoring();
        return ResponseEntity.ok(Map.of("status", "reset complete"));
    }
}
