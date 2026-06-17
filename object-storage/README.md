# object-storage

S3 호환 오브젝트 스토리지 **SeaweedFS vs Garage** 비교 학습 PoC.
**같은 Spring Boot 코드**로 두 백엔드를 호출하고, Thymeleaf UI에서 CRUD와 성능 벤치마크를 실행한다.

> 📊 자세한 비교/아키텍처/벤치마크 → **[docs/object-storage-poc-report.md](docs/object-storage-poc-report.md)**

## 빠른 시작

```powershell
# 1) 두 스토리지 기동 + Garage 부트스트랩(layout/bucket/key, 멱등)
./scripts/up.ps1

# 2) 앱 실행
./gradlew bootRun        # → http://localhost:8092

# 3) 정리
./scripts/down.ps1         # 정지(데이터 유지)
./scripts/down.ps1 -Reset  # 정지 + 데이터 초기화
```

## 엔드포인트

| 대상 | URL |
|---|---|
| 앱 UI | http://localhost:8092 |
| SeaweedFS S3 / Master UI / Admin | :8333 / :9333 / :23646 |
| Garage S3 / Admin API | :3900 / :3903 |

## 구조

```
object-storage/
├── docker/
│   ├── docker-compose.yml      # SeaweedFS(mini) + Garage 단일노드
│   ├── garage.toml             # Garage 설정
│   └── garage-init.ps1         # Garage 부트스트랩(멱등)
├── scripts/                    # up.ps1 / down.ps1
├── src/main/java/.../objectstorage/
│   ├── config/                 # StorageProperties, S3ClientConfig(2개 빈)
│   ├── storage/                # Backend enum, S3StorageService(백엔드 파라미터화)
│   ├── benchmark/              # BenchmarkService / Result
│   └── web/                    # File CRUD · Backend 상태 · Benchmark API + 뷰
├── src/main/resources/
│   ├── application.yml         # storage.backends.{seaweedfs,garage}
│   └── templates/index.html    # 대시보드 UI
└── docs/                       # 비교 리포트 + 스크린샷
```

## 핵심 포인트

- 두 백엔드 모두 AWS SDK v2 `S3Client` 로 접근 — **endpoint/region/credentials만 다르고 코드는 동일**.
- 비-AWS S3 필수 설정: `forcePathStyle(true)` + 체크섬 `WHEN_REQUIRED`
  (안 하면 Garage 업로드가 `Invalid payload signature(400)` 로 실패 — 리포트 참고).
- SeaweedFS는 `S3_BUCKET` env로 버킷 자동생성, Garage는 `garage-init.ps1` 로 부트스트랩.

## REST API (`{backend}` = `seaweedfs` | `garage`)

```
POST   /api/{backend}/files            multipart 'file' (+선택 'key')
GET    /api/{backend}/files?prefix=
GET    /api/{backend}/files/content?key=
DELETE /api/{backend}/files?key=
DELETE /api/{backend}/files/all
GET    /api/backends                   두 백엔드 상태
POST   /api/benchmark {count,size}     양쪽 동시 벤치마크
```
