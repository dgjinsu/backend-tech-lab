package com.example.seaweedpoc.storage;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * SeaweedFS S3 게이트웨이에 대한 업로드/다운로드/목록/삭제 로직.
 *
 * 여기서 쓰는 코드는 "진짜 AWS S3"를 쓸 때와 100% 동일하다.
 * 즉 나중에 운영에서 AWS S3로 갈아타도 이 서비스 코드는 그대로 둔 채
 * S3Config의 endpoint만 바꾸면 된다. 이것이 S3 호환 스토리지의 핵심 가치.
 */
@Service
public class S3StorageService {

    private final S3Client s3;
    private final String bucket;

    public S3StorageService(S3Client s3, @Value("${seaweedfs.s3.bucket}") String bucket) {
        this.s3 = s3;
        this.bucket = bucket;
    }

    /** 앱 시작 시 버킷이 없으면 생성한다. */
    @PostConstruct
    void ensureBucket() {
        try {
            s3.headBucket(b -> b.bucket(bucket));
            // 이미 존재
        } catch (NoSuchBucketException e) {
            s3.createBucket(b -> b.bucket(bucket));
        }
    }

    /** 파일 업로드. key는 S3 객체 키(예: "img/logo.png"). */
    public String upload(String key, byte[] content, String contentType) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();
        var resp = s3.putObject(req, RequestBody.fromBytes(content));
        return resp.eTag();
    }

    /** 파일 다운로드. 바이트와 contentType을 함께 반환. */
    public DownloadResult download(String key) {
        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        ResponseBytes<GetObjectResponse> bytes = s3.getObjectAsBytes(req);
        return new DownloadResult(bytes.asByteArray(), bytes.response().contentType());
    }

    /** prefix로 객체 목록 조회. */
    public List<ObjectInfo> list(String prefix) {
        ListObjectsV2Request req = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix == null ? "" : prefix)
                .build();
        ListObjectsV2Response resp = s3.listObjectsV2(req);
        return resp.contents().stream()
                .map(o -> new ObjectInfo(o.key(), o.size(), o.eTag()))
                .toList();
    }

    /** 단일 객체 삭제. */
    public void delete(String key) {
        s3.deleteObject(b -> b.bucket(bucket).key(key));
    }

    /** 버킷 비우기(모든 객체 삭제) — POC 정리용. */
    public int deleteAll() {
        List<ObjectInfo> all = list("");
        if (all.isEmpty()) {
            return 0;
        }
        List<software.amazon.awssdk.services.s3.model.ObjectIdentifier> ids = all.stream()
                .map(o -> software.amazon.awssdk.services.s3.model.ObjectIdentifier.builder().key(o.key()).build())
                .toList();
        s3.deleteObjects(b -> b.bucket(bucket).delete(Delete.builder().objects(ids).build()));
        return ids.size();
    }

    // --- 응답 DTO ---
    public record DownloadResult(byte[] content, String contentType) {}

    public record ObjectInfo(String key, Long size, String eTag) {}
}
