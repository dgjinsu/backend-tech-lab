package com.example.objectstorage.web;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.objectstorage.storage.Backend;
import com.example.objectstorage.storage.BackendStatus;
import com.example.objectstorage.storage.S3StorageService;

/** UI 토글/상태 배지용: 설정된 백엔드 목록과 헬스 상태를 반환. */
@RestController
@RequestMapping("/api/backends")
public class BackendController {

    private final S3StorageService storage;

    public BackendController(S3StorageService storage) {
        this.storage = storage;
    }

    @GetMapping
    public List<BackendStatus> backends() {
        return Arrays.stream(Backend.values())
                .map(storage::status)
                .toList();
    }
}
