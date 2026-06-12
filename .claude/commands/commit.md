---
description: 변경사항을 스테이징하고 저장소 컨벤션에 맞는 메시지로 커밋
argument-hint: "[추가 지시사항 (선택)]"
allowed-tools: Bash(git add:*), Bash(git status:*), Bash(git diff:*), Bash(git commit:*), Bash(git log:*), Bash(git rev-parse:*), Bash(git branch:*), Bash(git show:*)
---

## 현재 상태 (컨텍스트)

- 브랜치/상태: !`git status`
- 스테이징 안 된 변경 요약: !`git diff --stat`
- 스테이징된 변경 요약: !`git diff --cached --stat`
- 최근 커밋 10개 (메시지 스타일 참고): !`git log --oneline -10`

## 작업

위 변경사항을 커밋한다.

1. **분석** — 무엇이/왜 바뀌었는지 변경 내용을 파악한다. 필요하면 `git diff`로 상세 확인.
2. **스테이징** — 관련 파일을 `git add`로 스테이징한다. 이미 스테이징된 게 있으면 그대로 사용. 서로 무관한 변경이 섞여 있으면 사용자에게 분리 커밋이 필요한지 짧게 확인한다.
3. **메시지 작성** — 이 저장소의 컨벤션을 따른다:
   - **한국어**로 작성
   - Conventional Commits 접두사: `feat:`, `fix:`, `docs:`, `chore:`, `refactor:`, `test:`, `style:`, `perf:`, `build:`, `ci:`
   - 제목은 50자 내외로 간결하게. 변경이 복잡하면 본문에 "무엇을/왜"를 불릿으로 추가.
4. **추가 지시사항 반영**: $ARGUMENTS
5. **커밋** — `git commit`으로 커밋한다.
6. **결과 확인** — `git log --oneline -1`로 방금 만든 커밋을 보여준다.

## 규칙

- **`git push`는 하지 않는다.** 사용자가 명시적으로 요청할 때만 push.
- `git commit --amend`, `git reset`, `git rebase` 등 히스토리를 바꾸는 명령은 사용자가 명시적으로 요청할 때만.
- 커밋할 변경이 전혀 없으면(=`git status`가 clean) 커밋하지 말고 그 사실만 알린다.
- 사용자가 이미 일부 파일만 `git add` 해 두었다면 그 의도를 존중해 스테이징된 것만 커밋한다.
