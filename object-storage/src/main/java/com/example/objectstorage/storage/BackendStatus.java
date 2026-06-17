package com.example.objectstorage.storage;

/** UI 상태 배지/토글에 쓰는 백엔드 상태 스냅샷. */
public record BackendStatus(
        String backend,     // key (seaweedfs / garage)
        String label,       // 표시용 이름
        String endpoint,
        boolean healthy,
        String bucket,
        Integer objectCount,
        String error) {}
