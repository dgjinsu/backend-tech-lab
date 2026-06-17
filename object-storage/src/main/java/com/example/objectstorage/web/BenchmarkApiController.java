package com.example.objectstorage.web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.objectstorage.benchmark.BenchmarkResult;
import com.example.objectstorage.benchmark.BenchmarkService;
import com.example.objectstorage.storage.Backend;

/**
 * 벤치마크 실행 API.
 *
 *  POST /api/benchmark   body: {"count":100,"size":65536}
 *  → 두 백엔드를 순차로 측정해 결과를 함께 반환(UI 비교 + 리포트 수치 출처).
 */
@RestController
@RequestMapping("/api/benchmark")
public class BenchmarkApiController {

    private final BenchmarkService benchmark;

    public BenchmarkApiController(BenchmarkService benchmark) {
        this.benchmark = benchmark;
    }

    @PostMapping
    public Map<String, BenchmarkResult> run(@RequestBody(required = false) BenchmarkRequest req) {
        int count = (req == null || req.count() == null) ? 100 : Math.max(1, Math.min(2000, req.count()));
        int size = (req == null || req.size() == null) ? 64 * 1024 : Math.max(1, Math.min(50 * 1024 * 1024, req.size()));

        Map<String, BenchmarkResult> out = new LinkedHashMap<>();
        for (Backend b : Backend.values()) {
            out.put(b.key(), benchmark.run(b, count, size));
        }
        return out;
    }

    public record BenchmarkRequest(Integer count, Integer size) {}
}
