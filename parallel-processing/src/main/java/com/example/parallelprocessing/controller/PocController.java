package com.example.parallelprocessing.controller;

import com.example.parallelprocessing.dto.PocResult;
import com.example.parallelprocessing.service.PocService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/poc")
@RequiredArgsConstructor
public class PocController {

    private final PocService pocService;

    @PostMapping("/sequential")
    public ResponseEntity<PocResult> sequential() {
        PocResult result = pocService.executeSequential();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/thread-pool")
    public ResponseEntity<PocResult> threadPool(
            @RequestParam(defaultValue = "100") int threads) throws InterruptedException {
        PocResult result = pocService.executeWithThreadPool(threads);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/results")
    public ResponseEntity<List<PocResult>> results() {
        return ResponseEntity.ok(pocService.getResults());
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, String>> cleanup() throws Exception {
        pocService.cleanup();
        return ResponseEntity.ok(Map.of("message", "Cleanup completed"));
    }
}
