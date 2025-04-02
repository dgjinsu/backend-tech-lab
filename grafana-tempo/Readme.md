## Tempo
### 스토리지
- 템포는 객체 스토리지(Object Storage)에 데이터를 저장한다. 대표적으로 S3, minio, GCS 등이 있다.
- Tempo는 로그처럼 실시간 검색 가능한 DB를 사용하지 않고, trace 데이터를 chunk 단위로 압축해서 blob에 저장한다.
- 이 구조 덕분에
  - 운영 비용이 매우 낮고
  - 고가용/대용량 처리에 유리하고
  - 유지보수 부담도 적다.
```yaml
storage:
  trace:
    backend: s3
    s3:
      bucket: tempo-traces
      endpoint: s3.amazonaws.com
      region: us-east-1
      access_key: your-access-key
      secret_key: your-secret-key
      insecure: false
```


