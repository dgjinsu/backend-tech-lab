package com.example.parallelprocessing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Spring 스레드 풀 설정.
 *
 * ThreadPoolTaskExecutor 내부 구조:
 *
 *   ┌──────────────────────────────────────────────────────────────┐
 *   │                   ThreadPoolTaskExecutor                     │
 *   │                                                              │
 *   │  ┌──────────────────────┐    ┌───────────────────────────┐  │
 *   │  │   Fixed Threads      │    │     Queue (무제한 대기열)   │  │
 *   │  │                      │    │                           │  │
 *   │  │  core = max = 100    │◄───│  capacity = MAX_VALUE     │  │
 *   │  │                      │    │                           │  │
 *   │  │  100개 스레드가       │    │  100개 스레드가 모두 바쁘면 │  │
 *   │  │  동시에 파일 I/O 수행 │    │  나머지 작업은 여기서 대기  │  │
 *   │  │                      │    │  FIFO 순서로 처리          │  │
 *   │  └──────────────────────┘    └───────────────────────────┘  │
 *   └──────────────────────────────────────────────────────────────┘
 *
 * 작업 흐름 (10만 개 요청 기준):
 *   1. 작업 1~100     → 100개 스레드가 즉시 실행
 *   2. 작업 101~100,000 → 큐에서 대기 (reject 없음)
 *   3. 스레드 하나가 작업 완료 → 큐에서 다음 작업을 꺼내서 실행
 *   4. 큐가 빌 때까지 반복
 *
 * core = max 동일하게 설정한 이유:
 *   - 스레드 수를 고정하여 리소스 사용량을 예측 가능하게 관리
 *   - 추가 스레드 생성/회수 오버헤드 없이 안정적으로 동작
 *   - 큐가 무제한이므로 reject 발생하지 않음
 *
 * POC에서의 활용:
 *   - API 호출 시 threads 파라미터로 corePoolSize/maxPoolSize를 동적 변경
 *   - 스레드 수별 (10, 50, 100, 200) 성능 차이를 비교 테스트
 */
@Configuration
public class ExecutorConfig {

    @Bean("threadPoolExecutor")
    public ThreadPoolTaskExecutor threadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 고정 스레드 수: core = max = 100
        // 항상 100개 스레드만 사용하고, 추가 스레드를 생성하지 않음
        executor.setCorePoolSize(100);
        executor.setMaxPoolSize(100);

        // 큐 용량을 무제한으로 설정하여 작업이 reject되지 않도록 함
        // 100개 스레드가 모두 바쁘면 나머지 작업은 여기서 순서대로 대기
        executor.setQueueCapacity(Integer.MAX_VALUE);

        // 스레드 이름 접두사 (로그에서 "pool-1", "pool-2"로 식별 가능)
        executor.setThreadNamePrefix("pool-");

        // 애플리케이션 종료 시 큐에 남은 작업을 완료한 후 종료
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 종료 대기 최대 시간 (초). 이 시간 내에 완료되지 않으면 강제 종료
        executor.setAwaitTerminationSeconds(60);

        // Bean 등록 전에 내부 ThreadPoolExecutor를 즉시 초기화
        executor.initialize();

        return executor;
    }
}
