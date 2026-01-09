# Saramin 크롤러

사람인(Saramin) 채용 공고 사이트에서 회사 정보를 크롤링하는 Python 프로그램입니다.

## 기능

- 채용 공고 리스트 페이지에서 회사 URL 자동 추출
- 회사 상세 페이지에서 다음 정보 수집:
  - 회사 이름
  - 인원 수
  - 위치
  - 연봉
- CSV 파일로 저장 (엑셀에서 바로 열기 가능)

## 설치

### 1. Python 3.8 이상 설치 확인
```bash
python --version
```

### 2. 의존성 설치
```bash
pip install -r requirements.txt
```

### 3. 환경 변수 설정 (선택사항)
```bash
cp .env.example .env
# .env 파일 편집하여 설정 변경
```

## 사용법

### 기본 사용
```bash
python main.py --url "https://www.saramin.co.kr/zf_user/jobs/list/job-category?page=1&cat_kewd=291,238,235,292" --start 1 --end 5
```

### 출력 파일명 지정
```bash
python main.py --url "URL" --start 1 --end 10 --output my_companies.csv
```

## 파라미터

- `--url`: 채용 공고 리스트 페이지 URL (필수)
- `--start`: 시작 페이지 번호 (필수)
- `--end`: 끝 페이지 번호 (필수)
- `--output`: 출력 CSV 파일명 (선택, 기본: saramin_YYYYMMDD_HHMMSS.csv)

## 출력 형식

CSV 파일에는 다음 컬럼이 포함됩니다:
- 회사명
- 인원수 (정보 없으면 '-')
- 위치 (정보 없으면 '-')
- 연봉 (정보 없으면 '-')
- 출처URL
- 크롤링시각

## 주의사항

1. **크롤링 속도**: 사이트에 부담을 주지 않도록 요청 간 2-3초 대기
2. **사용 목적**: 개인적/교육적 목적으로만 사용
3. **법적 책임**: 사람인 이용약관을 준수해야 함
4. **Chrome 브라우저**: Selenium이 Chrome을 사용하므로 Chrome 설치 필요

## 문제 해결

### Chrome 브라우저가 열리지 않음
- Chrome 브라우저가 설치되어 있는지 확인
- `webdriver-manager`가 자동으로 ChromeDriver 다운로드

### 데이터가 추출되지 않음
- 사람인 웹사이트 구조가 변경되었을 수 있음
- 로그 파일(`logs/saramin_scraper.log`) 확인

### 크롤링 중 차단됨
- `.env` 파일에서 `MIN_DELAY`, `MAX_DELAY` 값을 늘림 (예: 5~10초)

## 라이선스

MIT License
