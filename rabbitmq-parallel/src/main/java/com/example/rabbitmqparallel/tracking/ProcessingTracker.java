package com.example.rabbitmqparallel.tracking;

import com.example.rabbitmqparallel.dto.MessageProcessingRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class ProcessingTracker {

    private final ConcurrentHashMap<String, MessageProcessingRecord> records = new ConcurrentHashMap<>();
    private final AtomicInteger currentConcurrent = new AtomicInteger(0);
    private final AtomicInteger peakConcurrent = new AtomicInteger(0);

    public void recordReceived(String messageId, int sequenceNumber, long publishedAt,
                               long receivedAt, String thread) {
        records.put(messageId, MessageProcessingRecord.builder()
                .messageId(messageId)
                .sequenceNumber(sequenceNumber)
                .publishedAt(publishedAt)
                .receivedAt(receivedAt)
                .consumerThread(thread)
                .build());
    }

    public void recordCompleted(String messageId, long startAt, long endAt,
                                List<MessageProcessingRecord.SubCallRecord> subCalls,
                                boolean success, String error) {
        MessageProcessingRecord record = records.get(messageId);
        if (record != null) {
            record.setProcessingStartAt(startAt);
            record.setProcessingEndAt(endAt);
            record.setSubCallRecords(subCalls);
            record.setParallelCallCount(subCalls.size());
            record.setSuccess(success);
            record.setErrorMessage(error);
        }
    }

    public void incrementConcurrent() {
        int current = currentConcurrent.incrementAndGet();
        peakConcurrent.updateAndGet(peak -> Math.max(peak, current));
    }

    public void decrementConcurrent() {
        currentConcurrent.decrementAndGet();
    }

    public Map<String, MessageProcessingRecord> getAllRecords() {
        return Map.copyOf(records);
    }

    public int getPeakConcurrent() {
        return peakConcurrent.get();
    }

    public int getCurrentConcurrent() {
        return currentConcurrent.get();
    }

    public int getTotalProcessed() {
        return (int) records.values().stream()
                .filter(r -> r.getProcessingEndAt() > 0)
                .count();
    }

    public int getTotalReceived() {
        return records.size();
    }

    public boolean isComplete(int expectedCount) {
        return getTotalProcessed() >= expectedCount;
    }

    public void reset() {
        records.clear();
        currentConcurrent.set(0);
        peakConcurrent.set(0);
    }
}
