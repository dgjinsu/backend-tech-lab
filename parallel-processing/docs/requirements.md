# JSON 파일 병렬 저장 POC - 요구사항 스펙

## 1. 프로젝트 개요

10,000개의 JSON 파일을 로컬 파일 시스템에 저장할 때, 어떤 병렬 처리 전략이 가장 효율적인지 비교하는 POC 프로젝트.

### 목표
- Java 21 Virtual Threads vs 기존 ThreadPool 방식의 파일 I/O 성능 비교
- 최적의 병렬 저장 전략을 실제 프로젝트에 도입하기 위한 근거 확보

---

## 2. 기술 스택

| 항목 | 버전 |
|------|------|
| Java | 21 |
| Spring Boot | 4.0.3 |
| Gradle | 9.3.1 |
| Lombok | (Spring Boot 관리) |

---

## 3. 저장 경로 및 파일 구조

### 기본 경로
```
/storage/poc/
```

### 폴더 분배 전략
10,000개 파일을 **100개 폴더 × 100개 파일**로 균등 분배한다.

```
/storage/poc/
├── batch_001/
│   ├── data_00001.json
│   ├── data_00002.json
│   ├── ...
│   └── data_00100.json
├── batch_002/
│   ├── data_00101.json
│   ├── ...
│   └── data_00200.json
├── ...
└── batch_100/
    ├── data_09901.json
    ├── ...
    └── data_10000.json
```

- 폴더명: `batch_001` ~ `batch_100` (3자리 zero-padding)
- 파일명: `data_00001.json` ~ `data_10000.json` (5자리 zero-padding)
- 각 폴더에 100개의 JSON 파일이 포함됨

---

## 4. JSON 더미 데이터 스펙

파일당 약 **200줄** 분량의 더미 데이터를 생성한다.

### 스키마 예시
```json
{
  "id": 1,
  "uuid": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-02-27T10:30:00",
  "metadata": {
    "source": "poc-generator",
    "version": "1.0",
    "batchId": "batch_001",
    "sequenceNumber": 1
  },
  "user": {
    "userId": 10001,
    "username": "user_10001",
    "email": "user_10001@example.com",
    "profile": {
      "firstName": "John",
      "lastName": "Doe",
      "age": 28,
      "department": "Engineering",
      "position": "Senior Developer"
    },
    "address": {
      "street": "123 Main St",
      "city": "Seoul",
      "state": "Seoul",
      "zipCode": "06100",
      "country": "KR"
    }
  },
  "transactions": [
    {
      "transactionId": "txn_00001",
      "type": "PURCHASE",
      "amount": 15000,
      "currency": "KRW",
      "status": "COMPLETED",
      "createdAt": "2026-02-27T09:00:00",
      "items": [
        {
          "itemId": "item_001",
          "name": "Product A",
          "quantity": 2,
          "unitPrice": 5000,
          "category": "ELECTRONICS"
        },
        {
          "itemId": "item_002",
          "name": "Product B",
          "quantity": 1,
          "unitPrice": 5000,
          "category": "BOOKS"
        }
      ]
    },
    {
      "transactionId": "txn_00002",
      "type": "REFUND",
      "amount": 5000,
      "currency": "KRW",
      "status": "PENDING",
      "createdAt": "2026-02-27T10:00:00",
      "items": [
        {
          "itemId": "item_003",
          "name": "Product C",
          "quantity": 1,
          "unitPrice": 5000,
          "category": "CLOTHING"
        }
      ]
    }
  ],
  "analytics": {
    "totalTransactions": 2,
    "totalAmount": 20000,
    "averageAmount": 10000,
    "categories": ["ELECTRONICS", "BOOKS", "CLOTHING"],
    "tags": ["frequent-buyer", "premium", "verified"]
  },
  "settings": {
    "notifications": {
      "email": true,
      "sms": false,
      "push": true
    },
    "preferences": {
      "language": "ko",
      "timezone": "Asia/Seoul",
      "currency": "KRW",
      "theme": "dark"
    }
  },
  "logs": [
    {"level": "INFO", "message": "User logged in", "timestamp": "2026-02-27T08:00:00"},
    {"level": "INFO", "message": "Viewed dashboard", "timestamp": "2026-02-27T08:05:00"},
    {"level": "INFO", "message": "Started transaction", "timestamp": "2026-02-27T09:00:00"},
    {"level": "WARN", "message": "Payment retry", "timestamp": "2026-02-27T09:01:00"},
    {"level": "INFO", "message": "Transaction completed", "timestamp": "2026-02-27T09:02:00"}
  ]
}
```

> 각 파일의 `id`, `uuid`, `user`, `transactions` 등의 값은 파일마다 고유하게 생성한다.

---

## 5. 병렬 처리 전략

### 전략 A: Virtual Threads (Java 21)
- `Executors.newVirtualThreadPerTaskExecutor()` 사용
- 10,000개 작업을 Virtual Thread로 실행
- JDK 21의 경량 스레드로 I/O 바운드 작업에 최적화

### 전략 B: Fixed ThreadPool
- `Executors.newFixedThreadPool(N)` 사용
- 스레드 수를 조절하며 테스트 (예: 10, 50, 100, 200)
- 기존 Java 멀티스레딩 방식

### 비교 항목

| 측정 항목 | 설명 |
|-----------|------|
| 총 소요 시간 | 10,000개 파일 저장 완료까지 걸린 시간 (ms) |
| 처리량 (Throughput) | 초당 저장 완료된 파일 수 (files/sec) |
| 피크 메모리 사용량 | 실행 중 최대 메모리 사용량 |
| CPU 사용률 | 실행 중 CPU 점유율 |
| 에러 발생 수 | 파일 저장 실패 건수 |

---

## 6. 실행 방식

### API 엔드포인트
각 전략별로 REST API를 제공하여 실행 및 결과를 확인한다.

```
POST /api/poc/virtual-threads    → Virtual Threads로 10,000개 파일 저장
POST /api/poc/thread-pool?threads=N → ThreadPool(N개 스레드)로 10,000개 파일 저장
GET  /api/poc/results             → 최근 실행 결과 조회
DELETE /api/poc/cleanup           → /storage/poc 내 파일 전체 삭제
```

### 실행 결과 응답 예시
```json
{
  "strategy": "VIRTUAL_THREADS",
  "totalFiles": 10000,
  "successCount": 10000,
  "failCount": 0,
  "totalTimeMs": 3250,
  "throughputPerSec": 3076.9,
  "peakMemoryMb": 256.5
}
```

---

## 7. 제약 사항

- 저장 경로: `/storage/poc` 고정
- 파일 수: 10,000개 고정
- JSON 인코딩: UTF-8
- 테스트 전 기존 파일은 cleanup API로 삭제 후 실행
- 동일한 머신에서 동일한 조건으로 비교 테스트 수행
