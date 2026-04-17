package com.budget.api.domain.user.entity;

import com.budget.api.domain.budget.entity.Budget;
import com.budget.api.domain.department.entity.Department;
import com.budget.api.domain.expense.entity.Expense;
import com.budget.api.domain.salary.entity.Salary;
import com.budget.api.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToMany(mappedBy = "user")
    private List<Salary> salaries = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Budget> budgets = new ArrayList<>();

    @Builder
    private User(String email, String password, String nickname, Role role, Department department) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.department = department;
    }

    public static User create(String email, String password, String nickname, Role role, Department department) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .role(role != null ? role : Role.EMPLOYEE)
                .department(department)
                .build();
    }
}
