package com.example.parallelprocessing.service;

import com.example.parallelprocessing.dto.PocResult;
import com.example.parallelprocessing.generator.DummyDataGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class PocService {

    private static final int TOTAL_FILES = 10_000;

    private final DummyDataGenerator dataGenerator;
    private final FileStorageService fileStorageService;
    private final ThreadPoolTaskExecutor threadPoolExecutor;
    private final List<PocResult> results = Collections.synchronizedList(new ArrayList<>());

    public PocService(
            DummyDataGenerator dataGenerator,
            FileStorageService fileStorageService,
            @Qualifier("threadPoolExecutor") ThreadPoolTaskExecutor threadPoolExecutor
    ) {
        this.dataGenerator = dataGenerator;
        this.fileStorageService = fileStorageService;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public PocResult executeSequential() {
        log.info("Starting Sequential strategy for {} files", TOTAL_FILES);

        int successCount = 0;
        int failCount = 0;

        long beforeMemory = getUsedMemory();
        long peakMemory = beforeMemory;

        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= TOTAL_FILES; i++) {
            try {
                Map<String, Object> data = dataGenerator.generate(i);
                fileStorageService.saveJsonFile(i, data);
                successCount++;
            } catch (Exception e) {
                log.error("Failed to save file {}: {}", i, e.getMessage());
                failCount++;
            }
            long current = getUsedMemory();
            if (current > peakMemory) peakMemory = current;
        }

        long totalTimeMs = System.currentTimeMillis() - startTime;
        double peakMemoryMb = (peakMemory - beforeMemory) / (1024.0 * 1024.0);

        PocResult result = buildResult("SEQUENTIAL", successCount, failCount, totalTimeMs, Math.max(peakMemoryMb, 0));
        results.add(result);

        log.info("Sequential completed: {}ms, success={}, fail={}", totalTimeMs, successCount, failCount);
        return result;
    }

    public PocResult executeWithThreadPool(int threadCount) throws InterruptedException {
        log.info("Starting ThreadPool strategy (threads={}) for {} files", threadCount, TOTAL_FILES);

        // 스레드 수 변경 시 core > max 위반 방지를 위해 순서 조절
        // 늘릴 때: max 먼저 (core=10→50일 때 max가 10이면 core > max 위반)
        // 줄일 때: core 먼저 (max=100→10일 때 core가 100이면 core > max 위반)
        if (threadCount > threadPoolExecutor.getCorePoolSize()) {
            threadPoolExecutor.setMaxPoolSize(threadCount);
            threadPoolExecutor.setCorePoolSize(threadCount);
        } else {
            threadPoolExecutor.setCorePoolSize(threadCount);
            threadPoolExecutor.setMaxPoolSize(threadCount);
        }

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(TOTAL_FILES);

        long beforeMemory = getUsedMemory();
        long[] peakMemory = {beforeMemory};

        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= TOTAL_FILES; i++) {
            final int fileId = i;
            threadPoolExecutor.execute(() -> {
                try {
                    Map<String, Object> data = dataGenerator.generate(fileId);
                    fileStorageService.saveJsonFile(fileId, data);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.error("Failed to save file {}: {}", fileId, e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    trackPeakMemory(peakMemory);
                    latch.countDown();
                }
            });
        }

        latch.await();

        long totalTimeMs = System.currentTimeMillis() - startTime;
        double peakMemoryMb = (peakMemory[0] - beforeMemory) / (1024.0 * 1024.0);

        String strategyName = "THREAD_POOL_" + threadCount;
        PocResult result = buildResult(strategyName, successCount.get(), failCount.get(), totalTimeMs, Math.max(peakMemoryMb, 0));
        results.add(result);

        log.info("ThreadPool({}) completed: {}ms, success={}, fail={}", threadCount, totalTimeMs, successCount.get(), failCount.get());
        return result;
    }

    public List<PocResult> getResults() {
        return List.copyOf(results);
    }

    public void cleanup() throws Exception {
        fileStorageService.cleanup();
        results.clear();
        log.info("Cleanup completed");
    }

    private PocResult buildResult(String strategy, int success, int fail, long totalTimeMs, double peakMemoryMb) {
        double throughput = totalTimeMs > 0 ? (success * 1000.0 / totalTimeMs) : 0;
        return PocResult.builder()
                .strategy(strategy)
                .totalFiles(TOTAL_FILES)
                .successCount(success)
                .failCount(fail)
                .totalTimeMs(totalTimeMs)
                .throughputPerSec(Math.round(throughput * 10.0) / 10.0)
                .peakMemoryMb(Math.round(peakMemoryMb * 10.0) / 10.0)
                .build();
    }

    private long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private void trackPeakMemory(long[] peakMemory) {
        long current = getUsedMemory();
        if (current > peakMemory[0]) {
            peakMemory[0] = current;
        }
    }
}
