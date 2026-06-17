package com.example.objectstorage.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.objectstorage.storage.Backend;
import com.example.objectstorage.storage.DownloadResult;
import com.example.objectstorage.storage.ObjectInfo;
import com.example.objectstorage.storage.S3StorageService;

/**
 * 백엔드를 경로로 받는 파일 CRUD REST API. {backend} = seaweedfs | garage
 *
 *  POST   /api/{backend}/files            (multipart 'file', 선택 'key')  업로드
 *  GET    /api/{backend}/files            (선택 ?prefix=)                  목록
 *  GET    /api/{backend}/files/content    (?key=)                          다운로드
 *  DELETE /api/{backend}/files            (?key=)                          단일 삭제
 *  DELETE /api/{backend}/files/all        (선택 ?prefix=)                  일괄 삭제
 */
@RestController
@RequestMapping("/api/{backend}/files")
public class FileApiController {

    private final S3StorageService storage;

    public FileApiController(S3StorageService storage) {
        this.storage = storage;
    }

    @PostMapping
    public Map<String, Object> upload(
            @PathVariable String backend,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "key", required = false) String key) throws IOException {
        Backend b = Backend.from(backend);
        String objectKey = (key == null || key.isBlank()) ? file.getOriginalFilename() : key;
        String contentType = file.getContentType() != null
                ? file.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        String eTag = storage.upload(b, objectKey, file.getBytes(), contentType);
        return Map.of(
                "backend", b.key(),
                "key", objectKey,
                "size", file.getSize(),
                "contentType", contentType,
                "eTag", eTag);
    }

    @GetMapping
    public List<ObjectInfo> list(
            @PathVariable String backend,
            @RequestParam(value = "prefix", required = false) String prefix) {
        return storage.list(Backend.from(backend), prefix);
    }

    @GetMapping("/content")
    public ResponseEntity<byte[]> download(
            @PathVariable String backend,
            @RequestParam("key") String key) {
        DownloadResult result = storage.download(Backend.from(backend), key);
        String filename = key.contains("/") ? key.substring(key.lastIndexOf('/') + 1) : key;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        result.contentType() != null ? result.contentType()
                                : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(filename).build().toString())
                .body(result.content());
    }

    @DeleteMapping
    public Map<String, String> delete(
            @PathVariable String backend,
            @RequestParam("key") String key) {
        storage.delete(Backend.from(backend), key);
        return Map.of("backend", backend, "deleted", key);
    }

    @DeleteMapping("/all")
    public Map<String, Object> deleteAll(
            @PathVariable String backend,
            @RequestParam(value = "prefix", required = false) String prefix) {
        int n = storage.deleteAll(Backend.from(backend), prefix == null ? "" : prefix);
        return Map.of("backend", backend, "deletedCount", n);
    }

    /** 잘못된 백엔드 이름 → 400. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badBackend(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }
}
