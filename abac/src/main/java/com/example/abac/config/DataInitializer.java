package com.example.abac.config;

import com.example.abac.domain.department.Department;
import com.example.abac.domain.department.DepartmentRepository;
import com.example.abac.domain.user.Role;
import com.example.abac.domain.user.User;
import com.example.abac.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// H2 인메모리라 서버 띄울 때마다 DB가 비어 있음 → 테스트 계정을 부트 시점에 채워 넣는다.
// CommandLineRunner는 ApplicationContext가 다 올라온 뒤 한 번 실행됨.
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String DEFAULT_PASSWORD = "pass";

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // 재실행 안전장치: 이미 시딩됐으면 그대로 둔다(프로파일에 따라 DB가 영속일 때 대비).
        if (userRepository.count() > 0) {
            return;
        }

        // [ABAC 속성축] 3개 부서를 먼저 심고, 사용자에 departmentId를 연결.
        // sameDept 판정(같은 부서인지)이 이 departmentId 값을 그대로 쓴다.
        Department engineering = departmentRepository.save(new Department("Engineering"));
        Department sales = departmentRepository.save(new Department("Sales"));
        Department finance = departmentRepository.save(new Department("Finance"));

        // 평문 저장 금지. BCrypt 해시를 거친 값만 DB로.
        String encoded = passwordEncoder.encode(DEFAULT_PASSWORD);

        // [권한 매트릭스 시드]
        // emp1/emp2: EMPLOYEE — 본인 건만 조회/수정
        // mgr1: MANAGER (Engineering) — 자기 부서, 한도 내에서 승인
        // fin1: FINANCE — APPROVED 건 지급, 전사 조회는 APPROVED 이상
        // admin1: ADMIN — 한도 초과 승인 포함 모두 가능
        userRepository.save(new User("emp1", encoded, Role.EMPLOYEE, engineering.getId()));
        userRepository.save(new User("emp2", encoded, Role.EMPLOYEE, sales.getId()));
        userRepository.save(new User("mgr1", encoded, Role.MANAGER, engineering.getId()));
        userRepository.save(new User("fin1", encoded, Role.FINANCE, finance.getId()));
        userRepository.save(new User("admin1", encoded, Role.ADMIN, finance.getId()));

        log.info("Seeded 3 departments and 5 users. Default password: '{}'", DEFAULT_PASSWORD);
    }
}
