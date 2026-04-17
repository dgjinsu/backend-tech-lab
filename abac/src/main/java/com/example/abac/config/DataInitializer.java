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
        if (userRepository.count() > 0) {
            return;
        }

        Department engineering = departmentRepository.save(new Department("Engineering"));
        Department sales = departmentRepository.save(new Department("Sales"));
        Department finance = departmentRepository.save(new Department("Finance"));

        String encoded = passwordEncoder.encode(DEFAULT_PASSWORD);

        userRepository.save(new User("emp1", encoded, Role.EMPLOYEE, engineering.getId()));
        userRepository.save(new User("emp2", encoded, Role.EMPLOYEE, sales.getId()));
        userRepository.save(new User("mgr1", encoded, Role.MANAGER, engineering.getId()));
        userRepository.save(new User("fin1", encoded, Role.FINANCE, finance.getId()));
        userRepository.save(new User("admin1", encoded, Role.ADMIN, finance.getId()));

        log.info("Seeded 3 departments and 5 users. Default password: '{}'", DEFAULT_PASSWORD);
    }
}
