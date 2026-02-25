# Phase 1: 프로젝트 초기 세팅 명세서

## 1. 개요

Phase 1은 가계부 서비스의 개발 기반을 구축하는 단계이다. Backend(Spring Boot), Frontend(React + Vite), 인프라(Docker Compose)를 각각 초기화하고, 이후 Phase에서 기능 개발이 원활하게 진행될 수 있도록 공통 설정과 프로젝트 구조를 확립한다.

### 완료 기준 (Definition of Done)
- [ ] BE: `./gradlew build` 성공, 애플리케이션 정상 기동
- [ ] FE: `npm run build` 성공, 개발 서버 정상 기동
- [ ] Docker Compose로 PostgreSQL, Redis 컨테이너 정상 실행
- [ ] BE에서 PostgreSQL, Redis 연결 확인
- [ ] FE에서 BE API 호출 시 CORS 에러 없이 통신 확인
- [ ] Flyway 마이그레이션 정상 동작 확인
- [ ] Swagger UI (`/swagger-ui.html`) 접근 가능

---

## 2. Backend 초기 세팅

### 2.1 프로젝트 생성

| 항목 | 값 |
|------|-----|
| 빌드 도구 | Gradle (Kotlin DSL) |
| Java 버전 | 17 |
| Spring Boot 버전 | 3.x (최신 안정 버전) |
| Group | `com.budget` |
| Artifact | `backend` |
| 패키징 | Jar |

### 2.2 의존성 (build.gradle.kts)

```kotlin
dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Database
    runtimeOnly("org.postgresql:postgresql")

    // Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // Swagger (SpringDoc)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}
```

### 2.3 패키지 구조

```
backend/src/main/java/com/budget/
├── BudgetApplication.java              # 메인 클래스
├── domain/
│   ├── user/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   ├── salary/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   ├── expense/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   ├── category/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   └── budget/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── entity/
│       └── dto/
├── global/
│   ├── config/
│   │   ├── SecurityConfig.java         # Spring Security 설정
│   │   ├── CorsConfig.java             # CORS 설정
│   │   ├── RedisConfig.java            # Redis 설정
│   │   ├── SwaggerConfig.java          # Swagger 설정
│   │   └── JpaAuditingConfig.java      # JPA Auditing 설정
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java # 전역 예외 핸들러
│   │   ├── ErrorCode.java              # 에러 코드 enum
│   │   ├── ErrorResponse.java          # 에러 응답 DTO
│   │   └── BusinessException.java      # 비즈니스 예외 클래스
│   ├── entity/
│   │   └── BaseTimeEntity.java         # 생성일/수정일 공통 엔티티
│   ├── dto/
│   │   └── ApiResponse.java            # 공통 API 응답 래퍼
│   └── security/
│       ├── JwtTokenProvider.java       # JWT 토큰 생성/검증
│       ├── JwtAuthenticationFilter.java# JWT 인증 필터
│       └── CustomUserDetailsService.java
└── infra/
    └── redis/
        └── RedisService.java           # Redis 유틸리티
```

### 2.4 설정 파일

#### application.yml

```yaml
spring:
  profiles:
    active: local

  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
    properties:
      hibernate:
        format_sql: true
        default_batch_fetch_size: 100

  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration

server:
  port: 8080

springdoc:
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha
  api-docs:
    path: /v3/api-docs
```

#### application-local.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/budget_db
    username: budget_user
    password: budget_pass
    driver-class-name: org.postgresql.Driver

  data:
    redis:
      host: localhost
      port: 6379

  jpa:
    show-sql: true

jwt:
  secret: local-dev-secret-key-must-be-at-least-256-bits-long-for-hs256
  access-token-validity: 3600000      # 1시간 (ms)
  refresh-token-validity: 604800000   # 7일 (ms)

logging:
  level:
    com.budget: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql: TRACE
```

### 2.5 공통 클래스 구현 상세

#### BaseTimeEntity.java

```java
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

#### ApiResponse.java

```java
public record ApiResponse<T>(
    boolean success,
    T data,
    String message
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
```

#### ErrorCode.java

```java
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "C001", "잘못된 입력 값입니다."),
    INTERNAL_SERVER_ERROR(500, "C002", "서버 내부 오류가 발생했습니다."),
    ENTITY_NOT_FOUND(404, "C003", "대상을 찾을 수 없습니다."),

    // Auth
    INVALID_CREDENTIALS(401, "A001", "이메일 또는 비밀번호가 올바르지 않습니다."),
    EXPIRED_TOKEN(401, "A002", "만료된 토큰입니다."),
    INVALID_TOKEN(401, "A003", "유효하지 않은 토큰입니다."),
    DUPLICATE_EMAIL(409, "A004", "이미 사용 중인 이메일입니다."),

    // Salary
    SALARY_NOT_FOUND(404, "S001", "급여 정보를 찾을 수 없습니다."),
    DUPLICATE_SALARY(409, "S002", "해당 월의 급여가 이미 존재합니다."),

    // Expense
    EXPENSE_NOT_FOUND(404, "E001", "지출 내역을 찾을 수 없습니다."),

    // Category
    CATEGORY_NOT_FOUND(404, "CT001", "카테고리를 찾을 수 없습니다."),
    DUPLICATE_CATEGORY(409, "CT002", "이미 존재하는 카테고리입니다."),
    DEFAULT_CATEGORY_UNDELETABLE(400, "CT003", "기본 카테고리는 삭제할 수 없습니다."),

    // Budget
    BUDGET_NOT_FOUND(404, "B001", "예산 정보를 찾을 수 없습니다."),
    DUPLICATE_BUDGET(409, "B002", "해당 카테고리의 예산이 이미 존재합니다.");

    private final int status;
    private final String code;
    private final String message;
}
```

#### ErrorResponse.java

```java
public record ErrorResponse(
    int status,
    String code,
    String message,
    LocalDateTime timestamp
) {
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
            errorCode.getStatus(),
            errorCode.getCode(),
            errorCode.getMessage(),
            LocalDateTime.now()
        );
    }
}
```

#### BusinessException.java

```java
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
```

#### GlobalExceptionHandler.java

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.warn("BusinessException: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(e.getErrorCode());
        return ResponseEntity.status(response.status()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        log.warn("Validation failed: {}", message);
        ErrorResponse response = new ErrorResponse(400, "C001", message, LocalDateTime.now());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected error: ", e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.internalServerError().body(response);
    }
}
```

### 2.6 SecurityConfig (Phase 1 기본 설정)

Phase 1에서는 모든 엔드포인트를 허용하되, 인증 구조의 뼈대만 잡아둔다.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 2.7 CORS 설정

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:5173")  // Vite 기본 포트
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

### 2.8 초기 Flyway 마이그레이션

Phase 1에서 작성할 마이그레이션 파일:

```
backend/src/main/resources/db/migration/
├── V1__create_user_table.sql
├── V2__create_category_table.sql
├── V3__create_salary_table.sql
├── V4__create_expense_table.sql
└── V5__create_budget_table.sql
```

각 마이그레이션의 상세 DDL은 `docs/erd.md` 문서를 참조한다.

---

## 3. Frontend 초기 세팅

### 3.1 프로젝트 생성

```bash
npm create vite@latest frontend -- --template react-ts
cd frontend
npm install
```

### 3.2 의존성 설치

```bash
# 핵심 라이브러리
npm install react-router-dom
npm install @tanstack/react-query
npm install zustand
npm install recharts
npm install axios

# Tailwind CSS
npm install -D tailwindcss @tailwindcss/vite

# 유틸리티
npm install dayjs
npm install react-hot-toast
npm install react-icons

# 개발 도구
npm install -D @types/node
```

### 3.3 디렉터리 구조

```
frontend/src/
├── api/
│   ├── client.ts               # Axios 인스턴스 설정
│   ├── auth.ts                 # 인증 API
│   ├── salary.ts               # 급여 API
│   ├── expense.ts              # 지출 API
│   ├── category.ts             # 카테고리 API
│   ├── budget.ts               # 예산 API
│   └── statistics.ts           # 통계 API
├── components/
│   ├── common/
│   │   ├── Button.tsx
│   │   ├── Input.tsx
│   │   ├── Modal.tsx
│   │   ├── Loading.tsx
│   │   ├── ErrorBoundary.tsx
│   │   └── Layout.tsx          # 공통 레이아웃 (사이드바 + 헤더)
│   └── charts/                 # 차트 컴포넌트 (Phase 4에서 구현)
├── hooks/
│   ├── useAuth.ts
│   └── useDebounce.ts
├── pages/
│   ├── LoginPage.tsx
│   ├── SignupPage.tsx
│   ├── DashboardPage.tsx
│   ├── SalaryPage.tsx
│   ├── ExpensePage.tsx
│   ├── BudgetPage.tsx
│   └── NotFoundPage.tsx
├── stores/
│   └── authStore.ts            # 인증 상태 (Zustand)
├── types/
│   ├── auth.ts
│   ├── salary.ts
│   ├── expense.ts
│   ├── category.ts
│   ├── budget.ts
│   └── api.ts                  # 공통 API 응답 타입
├── utils/
│   ├── format.ts               # 금액 포맷, 날짜 포맷
│   └── storage.ts              # localStorage 유틸
├── App.tsx                     # 라우터 설정
├── main.tsx                    # 엔트리포인트
└── index.css                   # Tailwind 임포트
```

### 3.4 핵심 설정 파일

#### vite.config.ts

```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';
import path from 'path';

export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

#### tsconfig.json (paths 추가)

```json
{
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  }
}
```

#### src/index.css

```css
@import "tailwindcss";
```

### 3.5 Axios 인스턴스 (api/client.ts)

```typescript
import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';

const apiClient = axios.create({
  baseURL: '/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터: 토큰 자동 첨부
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터: 401 시 토큰 갱신 시도
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config;
    if (error.response?.status === 401 && originalRequest) {
      // TODO: Phase 2에서 refresh token 로직 구현
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

### 3.6 공통 타입 정의 (types/api.ts)

```typescript
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string | null;
}

export interface ErrorResponse {
  status: number;
  code: string;
  message: string;
  timestamp: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;       // 현재 페이지 (0-based)
  size: number;
  first: boolean;
  last: boolean;
}
```

### 3.7 인증 상태 스토어 (stores/authStore.ts)

```typescript
import { create } from 'zustand';

interface User {
  id: number;
  email: string;
  nickname: string;
}

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  setUser: (user: User) => void;
  clearUser: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isAuthenticated: false,
  setUser: (user) => set({ user, isAuthenticated: true }),
  clearUser: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    set({ user: null, isAuthenticated: false });
  },
}));
```

### 3.8 App.tsx 라우터 기본 구조

```tsx
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Toaster } from 'react-hot-toast';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
      staleTime: 1000 * 60 * 5,  // 5분
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          {/* 공개 라우트 */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />

          {/* 인증 필요 라우트 */}
          <Route element={<ProtectedRoute />}>
            <Route element={<Layout />}>
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
              <Route path="/dashboard" element={<DashboardPage />} />
              <Route path="/salary" element={<SalaryPage />} />
              <Route path="/expenses" element={<ExpensePage />} />
              <Route path="/budget" element={<BudgetPage />} />
            </Route>
          </Route>

          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </BrowserRouter>
      <Toaster position="top-right" />
    </QueryClientProvider>
  );
}

export default App;
```

---

## 4. Docker Compose 구성

### 4.1 docker-compose.yml

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    container_name: budget-postgres
    environment:
      POSTGRES_DB: budget_db
      POSTGRES_USER: budget_user
      POSTGRES_PASSWORD: budget_pass
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U budget_user -d budget_db"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: budget-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  postgres_data:
  redis_data:
```

### 4.2 실행 방법

```bash
# 컨테이너 시작
docker-compose up -d

# 상태 확인
docker-compose ps

# 로그 확인
docker-compose logs -f postgres
docker-compose logs -f redis

# 컨테이너 종료
docker-compose down

# 데이터 포함 종료 (초기화)
docker-compose down -v
```

---

## 5. 환경변수 관리

### 5.1 Backend (.env 또는 application-local.yml)

| 변수명 | 기본값 | 설명 |
|--------|--------|------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/budget_db` | DB 접속 URL |
| `SPRING_DATASOURCE_USERNAME` | `budget_user` | DB 사용자명 |
| `SPRING_DATASOURCE_PASSWORD` | `budget_pass` | DB 비밀번호 |
| `SPRING_DATA_REDIS_HOST` | `localhost` | Redis 호스트 |
| `SPRING_DATA_REDIS_PORT` | `6379` | Redis 포트 |
| `JWT_SECRET` | (로컬 전용 키) | JWT 서명 키 (256bit 이상) |
| `JWT_ACCESS_TOKEN_VALIDITY` | `3600000` | Access Token 유효기간 (ms) |
| `JWT_REFRESH_TOKEN_VALIDITY` | `604800000` | Refresh Token 유효기간 (ms) |

### 5.2 Frontend (.env)

```
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

> 주의: `.env` 파일은 `.gitignore`에 반드시 추가한다.

---

## 6. .gitignore 설정

### 6.1 프로젝트 루트 .gitignore

```gitignore
# IDE
.idea/
.vscode/
*.iml

# OS
.DS_Store
Thumbs.db

# Environment
.env
.env.local
```

### 6.2 Backend .gitignore

```gitignore
# Gradle
.gradle/
build/
!gradle/wrapper/gradle-wrapper.jar

# IDE
.idea/
*.iml
out/

# Logs
*.log
```

### 6.3 Frontend .gitignore

```gitignore
node_modules/
dist/
.env
.env.local
*.log
```

---

## 7. 개발 서버 실행 순서

```
1. Docker Compose 실행
   $ docker-compose up -d

2. Backend 실행
   $ cd backend
   $ ./gradlew bootRun

3. Frontend 실행
   $ cd frontend
   $ npm run dev

4. 접속 확인
   - FE: http://localhost:5173
   - BE API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - PostgreSQL: localhost:5432
   - Redis: localhost:6379
```

---

## 8. Phase 1 태스크 분배

### BE 개발자 태스크

| # | 태스크 | 우선순위 | 예상 산출물 |
|---|--------|----------|-------------|
| B1 | Spring Boot 프로젝트 생성 + Gradle 설정 | P0 | `backend/` 프로젝트 전체 |
| B2 | `application.yml`, `application-local.yml` 작성 | P0 | 설정 파일 |
| B3 | 공통 모듈 구현 (BaseTimeEntity, ApiResponse, ErrorCode, GlobalExceptionHandler) | P0 | `global/` 패키지 |
| B4 | SecurityConfig + CorsConfig 기본 설정 | P0 | `global/config/` |
| B5 | RedisConfig + SwaggerConfig 설정 | P1 | `global/config/` |
| B6 | Flyway 마이그레이션 파일 작성 (V1~V5) | P0 | `db/migration/` |
| B7 | 헬스체크 엔드포인트 작성 (`GET /api/v1/health`) | P1 | 연결 확인용 |

### FE 개발자 태스크

| # | 태스크 | 우선순위 | 예상 산출물 |
|---|--------|----------|-------------|
| F1 | React + Vite + TypeScript 프로젝트 생성 | P0 | `frontend/` 프로젝트 전체 |
| F2 | Tailwind CSS 설정 | P0 | CSS 설정 |
| F3 | 디렉터리 구조 생성 + 공통 타입 정의 | P0 | `types/`, `api/` |
| F4 | Axios 인스턴스 + 인터셉터 설정 | P0 | `api/client.ts` |
| F5 | React Router 기본 라우팅 설정 | P0 | `App.tsx` |
| F6 | TanStack Query Provider + Zustand 스토어 뼈대 | P1 | `stores/`, `App.tsx` |
| F7 | 공통 컴포넌트 (Layout, Button, Input, Loading) | P1 | `components/common/` |
| F8 | Vite proxy 설정 + 개발 서버 연동 확인 | P0 | `vite.config.ts` |

---

## 9. 참조 문서

- API 명세: `docs/api-spec.md`
- 데이터 모델(ERD): `docs/erd.md`
- 프로젝트 개요: `PROJECT.md`
- 에이전트 가이드: `CLAUDE.md`
