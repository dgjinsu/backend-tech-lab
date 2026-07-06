# PWM 주간보고 자동화 — 로컬 PoC 실행 가이드

[weekly-report-automation.md](weekly-report-automation.md) 요구사항을 로컬에서 **직접 테스트**할 수 있도록 구성한 환경입니다.
확정 결정(로컬 PoC / n8n Form+Data Table 수집 / Code 순수 템플릿 / Notion 출력)을 그대로 구현했습니다.

## 구성물

| 파일 | 용도 |
|---|---|
| `docker-compose.yml` | 로컬 n8n 실행 (타임존 Asia/Seoul, 웹훅 localhost) |
| `.env` | 암호화 키 (이미 생성됨 · git 제외 · **백업 필수**) |
| `.env.example` | 키 생성 방법 안내용 샘플 |
| `workflows/A_weekly_report_collect.json` | **워크플로우 A** — 폼 → 파생값 → Data Table 저장 |
| `workflows/B_weekly_report_compile.json` | **워크플로우 B** — 조회 → Code 템플릿 → Notion 페이지 생성 |
| `files/` | 파일 기반 대체 저장/출력용(선택) |

```
[A] 주간보고 폼(Form) ─▶ 파생값 추가(weekKey/submittedAt) ─▶ Data Table Insert
[B] Manual/Schedule ─▶ Data Table Get(이번 주) ─▶ Code(Notion 블록) ─▶ HTTP(Notion 페이지 생성)
```

---

## STEP 1. n8n 실행

```powershell
cd D:\backend-tech-lab\n8n-workflow
docker compose up -d
docker compose logs -f n8n   # "Editor is now accessible" 뜨면 준비 완료 (Ctrl+C로 로그 종료)
```

- 브라우저에서 **http://localhost:5678** 접속.
- Docker Desktop이 실행 중이어야 합니다. (미실행 시 `docker compose` 가 데몬 접속 오류)

## STEP 2. 최초 계정 생성

첫 접속 시 owner 계정(이메일/비밀번호)을 만듭니다. 로컬 전용이므로 아무 값이나 가능(기억만 할 것).

## STEP 3. Data Table 생성 — `weekly_submissions`

> 워크플로우가 참조하는 저장소입니다. **워크플로우 import 전에 먼저 만들어야** 노드에서 선택할 수 있습니다.

1. 왼쪽 사이드바 상단 프로젝트에서 **Data tables**(또는 Overview → Data tables) 진입 → **Create Data Table**.
2. 이름: `weekly_submissions`
3. 아래 6개 컬럼 추가 (타입 전부 **String**). `id / createdAt / updatedAt` 는 자동 생성되니 그대로 둡니다.

   | 컬럼명 | 타입 |
   |---|---|
   | `weekKey` | String |
   | `author` | String |
   | `todo` | String |
   | `issue` | String |
   | `discuss` | String |
   | `submittedAt` | String |

   ⚠️ 컬럼명은 **철자·대소문자까지 정확히** 위와 같아야 합니다(폼 필드명과 자동 매핑됨).

> Data tables 메뉴가 안 보이면 n8n 버전이 낮은 것입니다 → 맨 아래 "대체: 파일 저장" 참고.

## STEP 4. Notion 사전 준비 (체크리스트)

워크플로우 B가 초안을 만들 대상입니다.

- [ ] **통합 생성**: https://www.notion.so/my-integrations → *New integration* (Internal) → **Secret 복사** (`ntn_...`)
- [ ] **DB 생성**: Notion에서 새 **데이터베이스**(표) 생성. 예: `PWM 주간보고`
      - 제목 속성명 확인 (기본 영어 `Name`, 한글 워크스페이스면 `이름`일 수 있음)
- [ ] **통합 연결**: 그 DB 페이지 우상단 `•••` → **Connections** → 방금 만든 통합 추가
      - ⚠️ 이 단계 누락이 404/권한오류의 최다 원인
- [ ] **database_id 확보**: DB를 브라우저 전체 페이지로 열고 URL에서 32자 ID 복사
      - `https://www.notion.so/<워크스페이스>/`**`0123456789abcdef0123456789abcdef`**`?v=...` ← 굵은 부분
- [ ] (제목 속성이 `이름` 이면) 워크플로우 B의 Code 노드에서 `"Name"` → `"이름"` 으로 변경 예정

## STEP 5. 워크플로우 A 가져오기 & 마무리

1. n8n 상단 **Overview → Create Workflow** → 우상단 `⋮` → **Import from File...** → `workflows/A_weekly_report_collect.json` 선택.
2. **`제출 저장 (Data Table Insert)` 노드 열기** → `Data Table` 항목이 비어 있음 → **From list**에서 `weekly_submissions` 선택.
   - Columns가 `Map Automatically` 인지 확인(폼 필드명이 컬럼명과 같아 자동 매핑됨).
3. 우상단 **Save** → **Active 토글 ON**.

## STEP 6. 워크플로우 B 가져오기 & 마무리

1. 같은 방식으로 `workflows/B_weekly_report_compile.json` import.
2. **`이번 주 제출분 조회 (Data Table)` 노드** → `Data Table`을 `weekly_submissions`로 선택.
   - 필터 조건이 `weekKey` **equals** `={{ $now.toFormat("kkkk-'W'WW") }}` 인지 확인.
3. **`회의록 블록 생성 (Code)` 노드** → 코드 안 `REPLACE_WITH_DATABASE_ID` 를 **STEP 4의 database_id**로 교체.
   - (제목 속성이 `이름`이면 `"Name"` → `"이름"` 도 함께 변경)
4. **`Notion 초안 생성 (HTTP)` 노드**:
   - `Authentication`이 **Generic Credential Type → Header Auth**로 설정돼 있음.
   - `Credential for Header Auth` → **Create new credential**:
     - `Name` = `Authorization`
     - `Value` = `Bearer ntn_...` (STEP 4의 Secret, **Bearer 뒤 공백 1칸** 포함)
   - 헤더 `Notion-Version = 2022-06-28` 이 들어있는지 확인(이미 설정됨).
5. **Save**.

## STEP 7. 로컬 검증 순서 (셀프 테스트)

1. **폼 제출 (A)** — `주간보고 폼 (Form)` 노드에서 **Open form**(또는 활성화된 폼 URL) 접속.
   - 작성자를 바꿔가며 **2~3회 제출**. 매번 "제출 완료되었습니다 ✅" 확인.
2. **저장 확인** — Data Table `weekly_submissions` 열기 → 방금 제출분이 **이번 주 weekKey**(예: `2026-W28`)로 쌓였는지 확인.
3. **취합 실행 (B)** — 워크플로우 B에서 **Test workflow**(= Manual Trigger) 클릭.
   - 각 노드 초록 체크 → `Notion 초안 생성 (HTTP)`가 **200** 응답인지 확인.
4. **결과 확인** — Notion DB에 `PWM 주간보고 초안 (2026-W28)` 페이지가 생성되고, 팀원별 보고가 채워졌는지 확인.

> ✅ 여기까지 성공하면 전체 파이프라인(수집→저장→취합→초안) 검증 완료입니다.

---

## 트러블슈팅

| 증상 | 원인 / 해결 |
|---|---|
| `docker compose` 데몬 오류 | Docker Desktop 미실행 → 실행 후 재시도 |
| 로그인/폼이 안 열림 | http(https 아님)로 접속. `N8N_SECURE_COOKIE=false` 이미 설정됨 |
| Data Table 노드가 빨간색/미완성 | 노드에서 `weekly_submissions` 다시 선택(import 시 링크가 풀림) |
| 저장 행의 author/todo가 비어있음 | 폼 노드 `typeVersion`이 낮아 `fieldName` 무시됨 → 이미지 `:latest` 사용 확인 |
| Notion `404 / could not find database` | ① 통합을 DB에 **Connections** 연결 안 함 ② database_id 오타 ③ 페이지(❌)가 아니라 **데이터베이스(✅)** 여야 함 |
| Notion `400 ... properties.Name` | DB 제목 속성명 불일치 → Code 노드 `"Name"`을 실제 속성명(`"이름"` 등)으로 변경 |
| Notion `401 unauthorized` | Header Auth 값이 `Bearer ntn_...` 형식인지(Bearer+공백) 확인 |
| B 실행 시 0건 | A와 B의 weekKey가 다른 주에 걸침, 또는 저장이 안 됨 → STEP 2 재확인 |

## 중지 / 정리

```powershell
docker compose down          # 컨테이너 중지 (데이터는 volume에 유지)
docker compose down -v       # 데이터까지 완전 삭제 (초기화)
```

---

## 참고: 확정 스펙과의 차이 / 구현 메모

- **폼 필드명(fieldName)** 을 `author/todo/issue/discuss` (ASCII)로 지정해, Data Table 컬럼과 **자동 매핑**되도록 했습니다. 덕분에 Set 노드는 요구사항대로 `weekKey`/`submittedAt`만 파생합니다.
- **Data Table 노드의 테이블 링크는 import 후 재선택이 필요**합니다(테이블 ID가 인스턴스마다 달라 JSON에 고정 불가). STEP 5·6에서 처리.
- Code 노드는 제출 0건일 때도 안전하게 "제출된 보고가 없습니다" 문단을 넣도록 보강했습니다.
- 자격증명(Notion Secret)은 워크플로우 JSON에 **포함하지 않습니다**. n8n Credential로 별도 저장(암호화)됩니다.

## 다음 단계 (요구사항 §8 — 143 배포 시)

1. `docker-compose.yml` 의 `WEBHOOK_URL` 을 `http://192.168.70.143:5678/` 로, 이미지 태그 고정, PostgreSQL 추가.
2. 워크플로우 A 앞단에 Schedule(목 10:00) + Slack DM(폼 링크 발송).
3. 워크플로우 B 끝단에 미제출자 Slack 리마인드.

---

### 대체: 파일 저장 (Data Tables 미지원 n8n인 경우)

Data tables 메뉴가 없으면, 저장을 로컬 JSON append로 바꿉니다.
- A: `제출 저장` 노드를 **Read/Write Files from Disk**(또는 Code로 `/files/weekly_submissions.json` append)로 교체.
- B: `조회` 노드를 파일 읽기 → Code에서 `weekKey` 필터로 교체.
- `docker-compose.yml` 의 `./files:/files` 마운트를 사용(이미 포함). 필요 시 알려주면 이 버전도 만들어 드립니다.
