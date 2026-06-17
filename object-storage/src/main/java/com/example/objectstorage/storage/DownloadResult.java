package com.example.objectstorage.storage;

/** 다운로드 결과: 바이트 + contentType. */
public record DownloadResult(byte[] content, String contentType) {}
