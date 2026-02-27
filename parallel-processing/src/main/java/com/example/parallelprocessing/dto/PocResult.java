package com.example.parallelprocessing.dto;

import lombok.Builder;

@Builder
public record PocResult(
        String strategy,
        int totalFiles,
        int successCount,
        int failCount,
        long totalTimeMs,
        double throughputPerSec,
        double peakMemoryMb
) {
}
