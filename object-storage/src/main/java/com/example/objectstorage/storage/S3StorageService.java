package com.example.objectstorage.storage;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.objectstorage.config.StorageProperties;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

/**
 * 두 S3 호환 백엔드(SeaweedFS, Garage)에 대한 공통 CRUD 서비스.
 *
 * 모든 메서드는 첫 인자로 {@link Backend} 를 받아 대상 백엔드를 고른다.
 * 실제 호출 코드(putObject/getObject/...)는 백엔드와 무관하게 100% 동일하다.
 */
@Service
public class S3StorageService {

    private static final Logger log = LoggerFactory.getLogger(S3StorageService.class);

    private final Map<Backend, S3Client> clients;
    private final StorageProperties props;

    public S3StorageService(Map<Backend, S3Client> clients, StorageProperties props) {
        this.clients = clients;
        this.props = props;
    }

    /** 앱 시작 시 각 백엔드 버킷이 있는지 확인하고, 없으면 생성을 시도한다(실패는 비치명적). */
    @PostConstruct
    void ensureBuckets() {
        clients.keySet().forEach(b -> {
            try {
                ensureBucket(b);
                log.info("[{}] 버킷 준비됨: {}", b.label(), bucket(b));
            } catch (Exception e) {
                log.warn("[{}] 버킷 확인 실패(무시): {}", b.label(), e.getMessage());
            }
        });
    }

    public void ensureBucket(Backend b) {
        S3Client s3 = client(b);
        String bucket = bucket(b);
        try {
            s3.headBucket(r -> r.bucket(bucket));
        } catch (S3Exception e) {
            // 없으면 생성 시도. Garage 앱 키는 버킷 생성 권한이 없을 수 있어(사전 생성 전제) 실패를 허용.
            try {
                s3.createBucket(r -> r.bucket(bucket));
            } catch (S3Exception ex) {
                log.warn("[{}] 버킷 생성 불가(사전 생성 필요): {}", b.label(), ex.awsErrorDetails().errorCode());
            }
        }
    }

    /** 업로드. key 는 S3 객체 키(예: "img/logo.png"). eTag 반환. */
    public String upload(Backend b, String key, byte[] content, String contentType) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket(b))
                .key(key)
                .contentType(contentType)
                .build();
        return client(b).putObject(req, RequestBody.fromBytes(content)).eTag();
    }

    /** 다운로드. 바이트 + contentType 반환. */
    public DownloadResult download(Backend b, String key) {
        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucket(b))
                .key(key)
                .build();
        ResponseBytes<GetObjectResponse> bytes = client(b).getObjectAsBytes(req);
        return new DownloadResult(bytes.asByteArray(), bytes.response().contentType());
    }

    /** prefix 로 객체 목록 조회. */
    public List<ObjectInfo> list(Backend b, String prefix) {
        ListObjectsV2Request req = ListObjectsV2Request.builder()
                .bucket(bucket(b))
                .prefix(prefix == null ? "" : prefix)
                .build();
        return client(b).listObjectsV2(req).contents().stream()
                .map(o -> new ObjectInfo(
                        o.key(),
                        o.size(),
                        o.eTag(),
                        o.lastModified() == null ? null : o.lastModified().toString()))
                .toList();
    }

    /** 단일 객체 삭제. */
    public void delete(Backend b, String key) {
        client(b).deleteObject(r -> r.bucket(bucket(b)).key(key));
    }

    /** prefix 에 해당하는 객체 일괄 삭제. 삭제 개수 반환. */
    public int deleteAll(Backend b, String prefix) {
        List<ObjectInfo> all = list(b, prefix);
        if (all.isEmpty()) {
            return 0;
        }
        List<ObjectIdentifier> ids = all.stream()
                .map(o -> ObjectIdentifier.builder().key(o.key()).build())
                .toList();
        client(b).deleteObjects(r -> r.bucket(bucket(b)).delete(Delete.builder().objects(ids).build()));
        return ids.size();
    }

    /** UI 배지용 상태 스냅샷. list("")가 성공하면 healthy 로 보고 객체 수도 함께 반환. */
    public BackendStatus status(Backend b) {
        StorageProperties.BackendProps p = props.getBackends().get(b.key());
        try {
            List<ObjectInfo> objs = list(b, "");
            return new BackendStatus(b.key(), b.label(), p.getEndpoint(), true, p.getBucket(), objs.size(), null);
        } catch (Exception e) {
            return new BackendStatus(b.key(), b.label(), p.getEndpoint(), false, p.getBucket(), null, e.getMessage());
        }
    }

    // --- 내부 헬퍼 ---

    private S3Client client(Backend b) {
        S3Client c = clients.get(b);
        if (c == null) {
            throw new IllegalArgumentException("설정되지 않은 백엔드: " + b);
        }
        return c;
    }

    private String bucket(Backend b) {
        return props.getBackends().get(b.key()).getBucket();
    }
}
