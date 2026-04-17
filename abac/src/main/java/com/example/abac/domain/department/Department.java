package com.example.abac.domain.department;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// [ABAC] 부서 자체는 단순 엔티티지만, User.departmentId와 Expense.departmentId를
// 잇는 '속성 축'. ExpensePolicy.sameDept()와 ExpenseSpecifications의 MANAGER 스코프가
// 이 값을 비교해서 판정하므로 도메인 모델에서 없어서는 안 될 고정축이다.
@Entity
@Getter
@Table(name = "departments")
// JPA 스펙상 no-arg 생성자가 필요하지만 외부에서 빈 Department를 만들지 못하도록 PROTECTED.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    public Department(String name) {
        this.name = name;
    }
}
