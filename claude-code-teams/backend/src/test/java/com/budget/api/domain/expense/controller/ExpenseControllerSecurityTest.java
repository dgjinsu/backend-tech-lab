package com.budget.api.domain.expense.controller;

import com.budget.api.domain.category.entity.Category;
import com.budget.api.domain.department.entity.Department;
import com.budget.api.domain.expense.dto.ExpenseCreateRequest;
import com.budget.api.domain.expense.dto.ExpenseResponse;
import com.budget.api.domain.expense.dto.ExpenseUpdateRequest;
import com.budget.api.domain.expense.entity.Expense;
import com.budget.api.domain.expense.repository.ExpenseRepository;
import com.budget.api.domain.expense.service.ExpenseService;
import com.budget.api.domain.user.entity.Role;
import com.budget.api.domain.user.entity.User;
import com.budget.api.global.config.SecurityConfig;
import com.budget.api.global.jwt.JwtAuthenticationFilter;
import com.budget.api.global.jwt.JwtTokenProvider;
import com.budget.api.global.security.ExpensePermissionEvaluator;
import com.budget.api.support.WithMockCustomUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * RBAC + ABAC 정책 매트릭스 검증.
 * - RBAC: role-based (EMPLOYEE/MANAGER/ADMIN)
 * - ABAC: department 속성 매칭
 */
@WebMvcTest(controllers = ExpenseController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, ExpensePermissionEvaluator.class})
class ExpenseControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private ExpenseRepository expenseRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private Department department(long id) {
        Department d = Department.create("D" + id);
        ReflectionTestUtils.setField(d, "id", id);
        return d;
    }

    private User userEntity(long id, Department d) {
        User u = User.create("u" + id + "@a.com", "pw", "user" + id, Role.EMPLOYEE, d);
        ReflectionTestUtils.setField(u, "id", id);
        return u;
    }

    private Expense expense(long id, long ownerId, long deptId) {
        Department d = department(deptId);
        User u = userEntity(ownerId, d);
        Category category = Category.createDefault("식비");
        ReflectionTestUtils.setField(category, "id", 1L);
        Expense e = Expense.create(u, category, d, 10000L, "점심", LocalDate.of(2026, 4, 17), null);
        ReflectionTestUtils.setField(e, "id", id);
        return e;
    }

    private ExpenseResponse expenseResponseStub(long id) {
        return new ExpenseResponse(
                id,
                new ExpenseResponse.CategoryInfo(1L, "식비", null, null),
                10000L, "점심", LocalDate.of(2026, 4, 17), null,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    // ===== READ =====

    @Test
    @DisplayName("READ: EMPLOYEE가 같은 부서 지출 조회 → 200")
    @WithMockCustomUser(userId = 10, role = Role.EMPLOYEE, departmentId = 1)
    void read_employee_sameDept_200() throws Exception {
        given(expenseRepository.findById(5L)).willReturn(Optional.of(expense(5L, 20L, 1L)));
        given(expenseService.getExpense(anyLong())).willReturn(expenseResponseStub(5L));

        mockMvc.perform(get("/api/v1/expenses/5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("READ: EMPLOYEE가 다른 부서 지출 조회 → 403")
    @WithMockCustomUser(userId = 10, role = Role.EMPLOYEE, departmentId = 1)
    void read_employee_otherDept_403() throws Exception {
        given(expenseRepository.findById(5L)).willReturn(Optional.of(expense(5L, 20L, 2L)));

        mockMvc.perform(get("/api/v1/expenses/5"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("READ: ADMIN은 다른 부서도 조회 → 200")
    @WithMockCustomUser(userId = 99, role = Role.ADMIN, departmentId = 9)
    void read_admin_anyDept_200() throws Exception {
        given(expenseService.getExpense(anyLong())).willReturn(expenseResponseStub(5L));

        mockMvc.perform(get("/api/v1/expenses/5"))
                .andExpect(status().isOk());
    }

    // ===== WRITE (update) =====

    @Test
    @DisplayName("WRITE: EMPLOYEE가 같은 부서 내 타인 지출 수정 → 403")
    @WithMockCustomUser(userId = 10, role = Role.EMPLOYEE, departmentId = 1)
    void write_employee_sameDeptOther_403() throws Exception {
        given(expenseRepository.findById(5L)).willReturn(Optional.of(expense(5L, 20L, 1L)));

        mockMvc.perform(put("/api/v1/expenses/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExpenseUpdateRequest(
                                1L, 5000L, "수정", LocalDate.of(2026, 4, 17), null))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("WRITE: EMPLOYEE가 본인 지출 수정 → 200")
    @WithMockCustomUser(userId = 10, role = Role.EMPLOYEE, departmentId = 1)
    void write_employee_own_200() throws Exception {
        given(expenseRepository.findById(5L)).willReturn(Optional.of(expense(5L, 10L, 1L)));
        given(expenseService.updateExpense(anyLong(), any(ExpenseUpdateRequest.class)))
                .willReturn(expenseResponseStub(5L));

        mockMvc.perform(put("/api/v1/expenses/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExpenseUpdateRequest(
                                1L, 5000L, "수정", LocalDate.of(2026, 4, 17), null))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("WRITE: MANAGER는 자기 부서 타인 지출도 수정 가능 → 200")
    @WithMockCustomUser(userId = 99, role = Role.MANAGER, departmentId = 1)
    void write_manager_sameDeptOther_200() throws Exception {
        given(expenseRepository.findById(5L)).willReturn(Optional.of(expense(5L, 20L, 1L)));
        given(expenseService.updateExpense(anyLong(), any(ExpenseUpdateRequest.class)))
                .willReturn(expenseResponseStub(5L));

        mockMvc.perform(put("/api/v1/expenses/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExpenseUpdateRequest(
                                1L, 5000L, "수정", LocalDate.of(2026, 4, 17), null))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("WRITE: MANAGER도 다른 부서는 수정 불가 → 403")
    @WithMockCustomUser(userId = 99, role = Role.MANAGER, departmentId = 1)
    void write_manager_otherDept_403() throws Exception {
        given(expenseRepository.findById(5L)).willReturn(Optional.of(expense(5L, 20L, 2L)));

        mockMvc.perform(put("/api/v1/expenses/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExpenseUpdateRequest(
                                1L, 5000L, "수정", LocalDate.of(2026, 4, 17), null))))
                .andExpect(status().isForbidden());
    }

    // ===== DELETE =====

    @Test
    @DisplayName("DELETE: ADMIN은 다른 부서 지출도 삭제 → 200")
    @WithMockCustomUser(userId = 99, role = Role.ADMIN, departmentId = 9)
    void delete_admin_any_200() throws Exception {
        mockMvc.perform(delete("/api/v1/expenses/5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE: EMPLOYEE가 타인 지출 삭제 → 403")
    @WithMockCustomUser(userId = 10, role = Role.EMPLOYEE, departmentId = 1)
    void delete_employee_other_403() throws Exception {
        given(expenseRepository.findById(5L)).willReturn(Optional.of(expense(5L, 20L, 1L)));

        mockMvc.perform(delete("/api/v1/expenses/5"))
                .andExpect(status().isForbidden());
    }

    // ===== CREATE =====

    @Test
    @DisplayName("CREATE: 인증된 모든 역할은 지출 생성 가능 → 201")
    @WithMockCustomUser(userId = 10, role = Role.EMPLOYEE, departmentId = 1)
    void create_employee_201() throws Exception {
        given(expenseService.createExpense(any(), any(ExpenseCreateRequest.class)))
                .willReturn(expenseResponseStub(5L));

        mockMvc.perform(post("/api/v1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExpenseCreateRequest(
                                1L, 10000L, "점심", LocalDate.of(2026, 4, 17), null))))
                .andExpect(status().isCreated());
    }
}
