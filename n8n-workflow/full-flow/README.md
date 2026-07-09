# PWM 주간보고 — 풀 플로우 (Slack + Notion)

자연어 스펙(월 08:00 수집 → 08:30 미제출 리마인드 → 취합 → 회의록 작성 → 완료 알림)을 n8n 워크플로우 **4개**로 구현했습니다.
기본 PoC(`../workflows` 의 A·B)의 확장판이며, **같은 n8n 인스턴스**(`../docker-compose.yml`)에서 돕니다.

> **출력처: Notion** (Monday는 개인 API 토큰 발급이 관리자 정책으로 막혀 보류). 회의록 출력만 Notion으로 바꾼 버전이 `04_compile_to_notion.json` 이고, Monday 버전(`04_compile_to_monday.json`)은 토큰 확보 시 쓸 수 있게 남겨뒀습니다.

## 요구사항 → 워크플로우 매핑

| # | 요구사항 | 구현 |
|---|---|---|
| 1 | 매주 월 08:00 정보 취합 | **02** Schedule `0 8 * * 1` → Slack DM으로 폼 링크 발송 |
| 2 | 4명(송충석·안성윤·김진수·한슬희)에게서 답변 | **01** 수집 폼(작성자 드롭다운) → Data Table 저장 |
| 3 | 08:30 미응답자에게 재촉 Slack | **03** Schedule `30 8 * * 1` → 미제출자 계산 → Slack DM |
| 4 | 4개 질문(팀 할일/프로젝트/PWM/휴가) | **01** 폼 필드 4개 |
| 5 | 취합 → "2026년 WW28 주간 회의" 회의록 작성 | **04(notion)** 부모 페이지 하위에 주간 페이지 생성 |
| 6 | 완료 알림 | **04** 마지막 Slack 알림 |

## 아키텍처

```
[01 수집 폼]  Form(작성자+4질문) → Set(weekKey) → Data Table(pwm_weekly) insert     (상시)
[02 안내]     월 08:00 → Code(로스터) → Slack DM(폼 링크) ×4
[03 리마인드] 월 08:30 → Data Table 조회 → Code(미제출자=로스터−제출자) → Slack DM
[04 취합]     Manual → Data Table 조회 → Code(회의록 블록)
                     → HTTP Notion 페이지 생성 → Code → Slack 완료 알림
```

---

## 사전 준비

### A. n8n 실행 (이미 있으면 생략)
`../README.md` STEP 1~2 참고. `docker compose up -d` → http://localhost:5678

### B. Data Table `pwm_weekly` 생성 (7컬럼, 전부 String)
`weekKey`, `author`, `teamTasks`, `projectActivity`, `pwmActivity`, `vacationPlan`, `submittedAt`
> 컬럼명은 폼 `fieldName`과 정확히 일치해야 자동 매핑됩니다.

### C. Slack 준비
- [ ] Slack 앱 → **Bot Token(`xoxb-...`)**, 스코프 `chat:write`(+ DM하려면 `im:write`)
- [ ] 앱 설치 + 완료알림 채널에 봇 초대
- [ ] **4명의 Slack member ID**(프로필 `•••` → "멤버 ID 복사", `U...`) 확보
- [ ] 완료알림 채널명(`#채널`) 또는 채널 ID

### D. Notion 준비 (관리자 승인 불필요 · 셀프 발급 · DB 안 씀)
- [x] [notion.so/my-integrations](https://www.notion.so/my-integrations) → *New integration*(Internal) → **Secret(`ntn_...`)** 복사
- [ ] 회의록을 모아둘 **부모 페이지** 하나 생성(예: `PWM 주간보고`) — **DB 아님, 그냥 일반 페이지**
- [ ] 그 페이지 `•••` → **Connections** 에 통합 연결 (누락 시 404)
- [ ] 페이지 URL 끝의 **32자 `page_id`** 확보 → 매주 이 페이지 **하위에** 회의록 페이지가 쌓임

---

## Import & 설정

### 공통: n8n 자격증명 2개 먼저 생성 (Credentials → Create Credential)
검색창에 아래 **내장 타입**을 검색해 고르고, 토큰만 붙여넣으면 됩니다.
| 자격증명 타입 | 입력 칸 | 값 |
|---|---|---|
| **Slack API** | Access Token | `xoxb-...` (봇 토큰) |
| **Notion API** | Internal Integration Secret | `ntn_...` |
> `Authorization` 헤더는 n8n이 자동으로 붙입니다. HTTP 노드는 이 자격증명을 `predefinedCredentialType`로 참조합니다. (Notion-Version 헤더는 워크플로우에 `2022-06-28`로 직접 지정돼 있음)

### 01_collect_form.json
1. Import → `제출 저장` 노드에서 Data Table `pwm_weekly` 선택.
2. `주간보고 폼` 노드의 **Form Path = `pwm-weekly`** 확인(→ 폼 URL `http://localhost:5678/form/pwm-weekly`).
3. Save → **Active ON**.

### 02_notify_open_mon0800.json
1. `수신자·메시지 생성 (Code)` → `U_REPLACE_*` 를 **실제 Slack member ID**로 교체.
2. `Slack DM 발송` → Credential = **Slack API**.
3. Save → (테스트 후) Active ON.

### 03_remind_missing_mon0830.json
1. `이번 주 제출분 조회` → Data Table `pwm_weekly` 선택.
2. `미제출자 계산 (Code)` → 로스터 `U_REPLACE_*` 교체.
3. `Slack 리마인드 발송` → Credential = **Slack API**.
4. Save → Active ON.

### 04_compile_to_notion.json  ← 지금 쓰는 버전
1. `이번 주 제출분 조회` → Data Table `pwm_weekly` 선택.
2. `회의 정보 조립 (Notion)` Code → `REPLACE_WITH_PARENT_PAGE_ID` 를 **부모 페이지 id**로 교체. (페이지 부모라 제목 속성명 이슈 없음)
3. `Notion 페이지 생성 (HTTP)` → Credential = **Notion API**.
4. `완료 알림 조립 (Code)` → `REPLACE_SLACK_CHANNEL` 교체.
5. `Slack 완료 알림` → Credential = **Slack API**.
6. Save. (PoC는 Manual 트리거. 자동화하려면 Schedule 노드 추가.)

> `04_compile_to_monday.json` 은 Monday 토큰이 생겼을 때 쓰는 대안 버전입니다(그룹 `____98157__1`, label "주간 회의", create_item+create_update). 지금은 안 써도 됩니다.

---

## ⚠️ 내가 내린 결정 (바꾸기 쉬움)

1. **출력처 = Notion 페이지**. Monday는 개인 API 토큰 발급이 막혀(엔터프라이즈 관리자 정책) 보류하고, 원래 요구사항 문서의 출력처였던 Notion으로 대체. 회의록은 Notion DB에 **한 페이지**로 생성(팀원별 보고를 블록으로). Monday 토큰 확보 시 `04_compile_to_monday.json` 으로 전환.
2. **취합(04) 트리거 = Manual**. 시점을 안 정해줘서 PoC는 수동. (예: 월 09:00 자동화하려면 Schedule 추가)
3. **수집 = 공용 폼 + 작성자 드롭다운**. 개인별 맞춤 링크는 후속 개선안.
4. **08:30 리마인드**: 08:00 후 30분은 촉박 — 스펙대로 넣었지만 03의 cron만 바꾸면 됨.
5. **Slack/Notion 모두 HTTP Request(raw API)** + n8n 내장 자격증명(`slackApi`/`notionApi`). API를 그대로 학습 가능.

---

## 검증 순서 (셀프 테스트)

1. **01** 활성화 → 폼(`/form/pwm-weekly`)에서 **작성자 바꿔가며 2~3명 제출**.
2. Data Table `pwm_weekly` 에 이번 주(`2026-Wxx`) 행이 쌓였는지 확인.
3. **03** 을 **Test workflow** → 미제출자에게만 리마인드 DM(0명이면 발송 없음).
4. **04(notion)** 을 **Test workflow** → Notion DB에 `2026년 WWxx 주간 회의` 페이지 생성 + 팀원별 보고 + 완료 채널 알림 확인.
5. (선택) **02** 수동 실행 → 4명에게 안내 DM.

## 트러블슈팅

| 증상 | 해결 |
|---|---|
| Slack `not_in_channel`/`channel_not_found` | 봇을 채널에 초대, 채널명/ID 확인. DM은 member ID(`U...`) |
| Slack `not_authed`/`invalid_auth` | **Slack API** 자격증명의 봇 토큰·스코프(`chat:write`) 확인 |
| Notion `404 could not find database` | 통합을 DB **Connections**에 연결했는지, `database_id` 오타, 페이지(❌)가 아닌 **데이터베이스(✅)**인지 |
| Notion `400 ... properties.Name` | DB 제목 속성명 불일치 → Code의 `"Name"`을 실제 속성명(`"이름"`)으로 |
| Notion `401 unauthorized` | **Notion API** 자격증명의 시크릿(`ntn_...`) 확인 |
| Data Table 노드 빨간색 | import 후 `pwm_weekly` 재선택 |
| Insert 시 `unknown column name 'formMode'` | Form 트리거가 `formMode` 등 여분 필드를 함께 내보냄 → Insert의 Columns를 **Map Each Column Manually**로 (7개 컬럼만 매핑, 나머지 무시) |
| Get 노드 `Condition`에 빨간 삼각형(`eq` 표시) | 값은 맞음. 컬럼 기반 동적 옵션이라 import 직후 검증이 안 됨 → 드롭다운에서 **Equals** 다시 선택하면 사라짐 |
| Get에서 `No output data returned` → 워크플로우 멈춤 | 이번 주 제출 0건. Get 노드 **Settings → Always Output Data ON** (JSON엔 반영됨) 이면 0건이어도 다음 노드 진행 → 03은 전원 리마인드, 04는 "제출 없음" 페이지 생성 |
| 실제 폼은 값이 Null인데 Execute step은 정상 | 에디터에서 고친 뒤 **저장·재게시(게시)** 안 함 → 라이브 폼은 옛 버전 실행. 저장 후 다시 게시 |
