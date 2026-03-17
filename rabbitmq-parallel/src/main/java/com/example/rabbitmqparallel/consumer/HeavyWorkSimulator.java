package com.example.rabbitmqparallel.consumer;

import com.example.rabbitmqparallel.dto.MessageProcessingRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
@Slf4j
public class HeavyWorkSimulator {

    private static final String[] CALL_TYPES = {
            "external-db", "internal-db", "user-api",
            "payment-api", "notification-svc", "cache-lookup",
            "file-storage", "analytics-api", "auth-svc", "search-api"
    };

    @Value("${simulation.parallel-calls-min}")
    private int minCalls;

    @Value("${simulation.parallel-calls-max}")
    private int maxCalls;

    @Value("${simulation.call-latency-min-ms}")
    private long minLatencyMs;

    @Value("${simulation.call-latency-max-ms}")
    private long maxLatencyMs;

    @Value("${simulation.failure-rate}")
    private double failureRate;

    private final Random random = new Random();

    public List<MessageProcessingRecord.SubCallRecord> simulateHeavyWork(String messageId)
            throws Exception {

        int callCount = random.nextInt(minCalls, maxCalls + 1);
        List<MessageProcessingRecord.SubCallRecord> results =
                Collections.synchronizedList(new ArrayList<>());

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();

            for (int i = 0; i < callCount; i++) {
                final int callIndex = i;
                final String callName = CALL_TYPES[callIndex % CALL_TYPES.length] + "-" + callIndex;

                futures.add(executor.submit(() -> {
                    long start = System.currentTimeMillis();
                    long sleepTime = random.nextLong(minLatencyMs, maxLatencyMs + 1);

                    Thread.sleep(sleepTime);

                    if (random.nextDouble() < failureRate) {
                        throw new RuntimeException("Simulated failure in " + callName);
                    }

                    long end = System.currentTimeMillis();
                    results.add(MessageProcessingRecord.SubCallRecord.builder()
                            .callName(callName)
                            .startAt(start)
                            .endAt(end)
                            .durationMs(end - start)
                            .success(true)
                            .build());

                    return null;
                }));
            }

            List<Exception> errors = new ArrayList<>();
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    errors.add((Exception) e.getCause());
                }
            }

            if (!errors.isEmpty()) {
                throw new RuntimeException(
                        errors.size() + " of " + callCount + " sub-calls failed for message " + messageId);
            }
        }

        return results;
    }
}
