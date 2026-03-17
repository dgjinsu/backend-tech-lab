package com.example.rabbitmqparallel.tracking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ResourceMonitor {

    private final List<ResourceSnapshot> snapshots = Collections.synchronizedList(new ArrayList<>());
    private volatile ScheduledExecutorService scheduler;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResourceSnapshot {
        private long timestamp;
        private int threadCount;
        private long usedMemoryBytes;
        private long maxMemoryBytes;
        private double usedMemoryMb;
    }

    public void startMonitoring() {
        snapshots.clear();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::takeSnapshot, 0, 5, TimeUnit.SECONDS);
        log.info("Resource monitoring started (5s interval)");
    }

    public void stopMonitoring() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            log.info("Resource monitoring stopped. Total snapshots: {}", snapshots.size());
        }
    }

    private void takeSnapshot() {
        Runtime rt = Runtime.getRuntime();
        long used = rt.totalMemory() - rt.freeMemory();
        snapshots.add(ResourceSnapshot.builder()
                .timestamp(System.currentTimeMillis())
                .threadCount(Thread.activeCount())
                .usedMemoryBytes(used)
                .maxMemoryBytes(rt.maxMemory())
                .usedMemoryMb(used / (1024.0 * 1024.0))
                .build());
    }

    public double getPeakMemoryMb() {
        return snapshots.stream()
                .mapToDouble(ResourceSnapshot::getUsedMemoryMb)
                .max().orElse(0);
    }

    public int getPeakThreadCount() {
        return snapshots.stream()
                .mapToInt(ResourceSnapshot::getThreadCount)
                .max().orElse(0);
    }

    public List<ResourceSnapshot> getSnapshots() {
        return List.copyOf(snapshots);
    }
}
