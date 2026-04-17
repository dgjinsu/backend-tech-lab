package com.example.abac.domain.department;

import org.springframework.data.jpa.repository.JpaRepository;

// 현재는 기본 CRUD만 필요. DataInitializer 시딩과 User.departmentId FK 검증 용도로만 쓰인다.
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
