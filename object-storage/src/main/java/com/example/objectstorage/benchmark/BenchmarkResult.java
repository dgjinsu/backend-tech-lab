package com.example.objectstorage.benchmark;

/**
 * 한 백엔드에 대한 벤치마크 측정 결과(시간 단위 ms, 처리량 MB/s).
 */
public record BenchmarkResult(
        String backend,
        String label,
        int objectCount,
        int objectSizeBytes,

        // 업로드
        double uploadTotalMs,
        double uploadAvgMs,
        double uploadP50Ms,
        double uploadP95Ms,
        double uploadThroughputMBps,

        // 다운로드
        double downloadTotalMs,
        double downloadAvgMs,
        double downloadP50Ms,
        double downloadP95Ms,
        double downloadThroughputMBps,

        // 정리(일괄 삭제)
        double deleteTotalMs) {}
