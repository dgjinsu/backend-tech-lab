package com.example.objectstorage.storage;

/** 객체 목록 항목. */
public record ObjectInfo(String key, Long size, String eTag, String lastModified) {}
