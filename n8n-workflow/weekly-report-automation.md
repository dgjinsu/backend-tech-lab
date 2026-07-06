# PWM 주간보고 자동화 (n8n) — 요구사항 정의

> 작성일: 2026-07-06
> 목적: 다른 채팅/세션에서 이 문서를 기준으로 구현을 이어가기 위한 자립형(self-contained) 요구사항 스펙.
> 상태: **설계 확정 · 구현 전 (n8n 미설치)**

---

## 1. 배경 & 목적

### As-Is (현재 주간보고)
- 프로젝트 차원에서 일정 · 할 일 · 이슈를 공유.
- 이후 각 팀원의 **금주 개발 안건 / 개발 이슈 / 논의사항**을 수동으로 취합.

### To-Be (개선 목표)
- n8n이 **정해진 시간에** 각 팀원에게서 그 주의 **할 일 · 보고사항 · 이슈**를 수집.
- 취합된 데이터를 **미리 만든 템플릿에 채워 주간보고 회의록 초안을 자동 생성**.
- 사람은 완성된 초안을 놓고 **바로 회의를 시작**.

---

## 2. 환경 & 현재 상태

| 항목 | 내용 |
|---|---|
| n8n | **미설치** (이번에 처음 도입) |
| 개발 PC | Windows 11 |
| 사내 인프라 | Docker 기반, `192.168.70.143` (추후 정식 배포 대상) |
| 문서 도구 | Notion (초안 출력 대상), Monday.com (기존 회의록/주간보고) |
| 메신저 | Slack (팀원 알림/링크 발송용, 후속 단계) |

---

## 3. 확정된 결정사항 (PoC 기준)

> 아래 4가지는 이미 확정됨. **재질문 없이 이 전제로 진행.**

| 항목 | 선택 | 이유 |
|---|---|---|
| **호스팅** | 로컬 PC 전용 (PoC) | 먼저 개인 PC에서 전체 파이프라인 검증. 리스크 최소화 |
| **수집 방식** | n8n **Form** → **Data Table** 저장 | 다인원·비동기 제출에 가장 안정적 (각자 아무 때나 제출) |
| **본문 생성** | **Code 노드 순수 템플릿** (외부 전송 없음) | LLM 미사용. 데이터가 밖으로 안 나감. 결정론적 |
| **출력 위치** | **Notion** 페이지 (HTTP Request로 Notion API 직접 호출) | 기존 문서 흐름과 연계 |

> ⚠️ 보안 참고: 본문 생성은 로컬(외부 전송 없음)이지만, **최종 초안은 Notion 클라우드로 업로드**됨. 사내 정책상 문제가 되면 출력 노드만 "로컬 파일" 또는 "사내 위키"로 교체.

---

## 4. 목표 아키텍처 (워크플로우 2개)

한 워크플로우로 "전원 응답 대기"는 사람마다 제출 시점이 달라 부적합 → **수집(A)과 취합(B)을 분리.**

```
[워크플로우 A — 수집]   (PoC: 수동/폼 직접 열기 · 운영: 매주 목 10:00 Schedule)
 (운영 시) Schedule ─▶ 팀원 목록 ─▶ Slack DM: "주간보고 폼 작성 → 링크"
        팀원이 폼 링크 클릭
              ▼
 [n8n Form]  금주 할 일 / 진행 이슈 / 논의사항 입력
              ▼
 Data Table(weekly_submissions)에 저장 ─▶ (운영 시) Slack "접수완료 ✅"

[워크플로우 B — 취합·초안]   (매주 금 09:00 Schedule + PoC용 Manual Trigger)
 Schedule ─▶ Data Table에서 '이번 주(weekKey)' 제출분 조회
          ─▶ Code: 순수 템플릿으로 Notion 블록 배열 생성
          ─▶ HTTP Request: Notion API로 초안 페이지 생성
          ─▶ (운영 시) 미제출자 Slack 리마인드
```

---

## 5. 상세 요구사항 (구현 스펙)

### 5.1 수집 폼 (워크플로우 A)
- **On Form Submission** (Form Trigger), Title: `PWM 주간보고`
- 필드:
  | 필드명 | 타입 | 필수 |
  |---|---|---|
  | 작성자 | Dropdown (팀원 이름 목록) | ✅ |
  | 금주 할 일 | Textarea | ✅ |
  | 진행 이슈 | Textarea | |
  | 논의사항 | Textarea | |
- **Edit Fields(Set)** 로 파생값 추가:
  - `weekKey` = `={{ $now.toFormat("kkkk-'W'WW") }}`  → 예: `2026-W28`
  - `submittedAt` = `={{ $now.toISO() }}`
- **Data Table → Insert Row** 로 저장
- **Form Ending**: "제출 완료되었습니다 ✅"

### 5.2 저장소 (Data Table `weekly_submissions`)
| 컬럼 | 타입 |
|---|---|
| weekKey | String |
| author | String |
| todo | String |
| issue | String |
| discuss | String |
| submittedAt | String |

> n8n 버전에 Data Tables 기능이 없으면 → "Read/Write Files from Disk"로 로컬 JSON append 방식으로 대체.

### 5.3 취합·초안 (워크플로우 B)
1. **Schedule Trigger** — Cron `0 9 * * 5` (금 09:00) + 테스트용 **Manual Trigger**
2. **Data Table → Get Row(s)** — 필터: `weekKey` equals `={{ $now.toFormat("kkkk-'W'WW") }}`
3. **Code 노드** (순수 템플릿 → Notion 블록 배열 생성):
   ```javascript
   const rows = $input.all().map(i => i.json);
   const week = rows[0]?.weekKey || $now.toFormat("kkkk-'W'WW");

   const H = (t) => ({ object:'block', type:'heading_2',
     heading_2:{ rich_text:[{ type:'text', text:{ content:t } }] } });
   const P = (t) => ({ object:'block', type:'paragraph',
     paragraph:{ rich_text:[{ type:'text', text:{ content:(t||'').slice(0,1900) } }] } });

   const children = [ H('프로젝트 일정 / 할 일 / 이슈'), P('(회의에서 작성)'), H('팀원별 보고') ];
   for (const r of rows) {
     children.push(P(`👤 ${r.author}\n• 금주 할 일: ${r.todo || '-'}\n• 이슈: ${r.issue || '없음'}\n• 논의사항: ${r.discuss || '없음'}`));
   }

   const body = {
     parent: { database_id: '여기에_DATABASE_ID' },
     properties: { "Name": { title: [ { text: { content: `PWM 주간보고 초안 (${week})` } } ] } },
     children
   };
   return [{ json: { body } }];
   ```
   > Notion DB 제목 속성이 `이름`이면 `"Name"` → `"이름"` 으로 변경.
4. **HTTP Request 노드** (Notion 페이지 생성):
   - Method `POST`, URL `https://api.notion.com/v1/pages`
   - Auth: Generic → **Header Auth** (`Authorization` = `Bearer ntn_...`)
   - Header 추가: `Notion-Version` = `2022-06-28`
   - Body: JSON = `={{ $json.body }}`

### 5.4 Notion 사전 준비
1. [notion.so/my-integrations](https://www.notion.so/my-integrations)에서 Internal Integration 생성 → Secret(`ntn_...`) 확보
2. DB 생성 (예: `PWM 주간보고`), 제목 속성명 확인
3. **DB → `•••` → Connections 에 통합 연결** (누락 시 API 404/권한오류)
4. DB URL에서 32자 `database_id` 확보

---

## 6. 범위 (Scope)

### ✅ PoC 포함
- 로컬 n8n 설치 (Docker `docker.n8n.io/n8nio/n8n` 또는 `npx n8n`)
- 워크플로우 A(폼→저장) + B(취합→템플릿→Notion) 완성
- 본인이 폼을 여러 번(작성자만 바꿔) 제출해 전체 흐름 셀프 검증

### ❌ PoC 제외 (후속 단계)
- 팀원 실제 접속 (로컬 폼 URL은 `localhost`라 본인만 열림)
- Slack DM 자동 발송 / 수집 알림
- 미제출자 자동 리마인드
- Schedule 기반 완전 자동화
- 사내 인프라(143) 배포

---

## 7. 제약 & 주의사항
- **`WEBHOOK_URL`** 은 팀원이 실제 접속하는 주소와 일치해야 함 (로컬=localhost, 운영=`http://192.168.70.143:5678/`).
- **`N8N_ENCRYPTION_KEY`** 고정·백업 필수 (미고정 시 재배포 때 저장된 자격증명 복호화 실패).
- 운영 배포 시 PostgreSQL + 버전 태그 고정 권장.
- Notion 통합을 대상 DB에 연결하는 것 잊지 말 것.
- 타임존 `Asia/Seoul` 설정.

---

## 8. 후속 단계 (PoC 이후, 143 배포 시)
1. docker-compose로 143에 배포 (n8n + PostgreSQL), `WEBHOOK_URL` = 사내 IP
2. 워크플로우 A 앞단에 Schedule(목 10:00) + Slack DM(폼 링크 발송) 추가
3. 워크플로우 B 끝단에 미제출자 Slack 리마인드 추가
4. (선택) 출력 대상 Notion → Monday 회의록 보드 등으로 확장/변경

---

## 9. 새 채팅에 붙여넣을 요청문 (복붙용)

```
n8n으로 PWM 주간보고를 자동화하려고 해.
같은 폴더의 weekly-report-automation.md 가 확정된 요구사항 문서야.

결정사항(로컬 PoC / n8n Form+Data Table 수집 / Code 순수 템플릿 / Notion 출력)은
이미 확정됐으니 다시 묻지 말고, 이 문서 기준으로 아래를 만들어줘:

- 워크플로우 A(수집), B(취합·초안)를 n8n에서 Import 가능한 JSON 2개로 생성
- 각 노드 설정과 Notion 사전 준비 체크리스트 포함
- 로컬에서 검증하는 순서 안내
```
