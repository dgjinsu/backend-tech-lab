package com.example.seaweedpoc.storage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.seaweedpoc.storage.S3StorageService.DownloadResult;
import com.example.seaweedpoc.storage.S3StorageService.ObjectInfo;

/**
 * SeaweedFS 파일 저장소를 다루는 REST API.
 *
 *  POST   /api/files            (multipart 'file', 선택 'key')  업로드
 *  GET    /api/files            (선택 ?prefix=)                  목록
 *  GET    /api/files/content    (?key=)                          다운로드
 *  DELETE /api/files            (?key=)                          삭제
 *  DELETE /api/files/all                                         전체 삭제(정리용)
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final S3StorageService storage;

    public FileController(S3StorageService storage) {
        this.storage = storage;
    }

    @PostMapping
    public Map<String, Object> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "key", required = false) String key) throws IOException {
        // key를 안 주면 업로드한 파일명을 그대로 키로 사용
        String objectKey = (key == null || key.isBlank()) ? file.getOriginalFilename() : key;
        String contentType = file.getContentType() != null
                ? file.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        String eTag = storage.upload(objectKey, file.getBytes(), contentType);
        return Map.of(
                "key", objectKey,
                "size", file.getSize(),
                "contentType", contentType,
                "eTag", eTag);
    }

    @GetMapping
    public List<ObjectInfo> list(@RequestParam(value = "prefix", required = false) String prefix) {
        return storage.list(prefix);
    }

    @GetMapping("/content")
    public ResponseEntity<byte[]> download(@RequestParam("key") String key) {
        DownloadResult result = storage.download(key);
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
    public Map<String, String> delete(@RequestParam("key") String key) {
        storage.delete(key);
        return Map.of("deleted", key);
    }

    @DeleteMapping("/all")
    public Map<String, Integer> deleteAll() {
        return Map.of("deletedCount", storage.deleteAll());
    }
}
