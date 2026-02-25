package com.budget.api.domain.expense.service;

import com.budget.api.domain.category.entity.Category;
import com.budget.api.domain.category.entity.CategoryType;
import com.budget.api.domain.category.repository.CategoryRepository;
import com.budget.api.domain.expense.dto.ExpenseCreateRequest;
import com.budget.api.domain.expense.dto.ExpenseResponse;
import com.budget.api.domain.expense.dto.ExpenseUpdateRequest;
import com.budget.api.domain.expense.entity.Expense;
import com.budget.api.domain.expense.repository.ExpenseRepository;
import com.budget.api.domain.user.entity.User;
import com.budget.api.domain.user.repository.UserRepository;
import com.budget.api.global.exception.CustomException;
import com.budget.api.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @InjectMocks
    private ExpenseService expenseService;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private User createTestUser() {
        User user = User.create("test@example.com", "encodedPassword", "테스터");
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private Category createTestCategory() {
        Category category = Category.createDefault("식비");
        ReflectionTestUtils.setField(category, "id", 1L);
        return category;
    }

    private Expense createTestExpense(User user, Category category) {
        Expense expense = Expense.create(user, category, 15000L, "점심 식사", LocalDate.of(2026, 2, 15), "회사 근처 식당");
        ReflectionTestUtils.setField(expense, "id", 1L);
        ReflectionTestUtils.setField(expense, "createdAt", LocalDateTime.of(2026, 2, 15, 12, 0));
        ReflectionTestUtils.setField(expense, "updatedAt", LocalDateTime.of(2026, 2, 15, 12, 0));
        return expense;
    }

    // ===== createExpense =====

    @Test
    @DisplayName("createExpense_정상요청_지출생성성공")
    void createExpense_정상요청_지출생성성공() {
        // given
        User user = createTestUser();
        Category category = createTestCategory();
        ExpenseCreateRequest request = new ExpenseCreateRequest(1L, 15000L, "점심 식사", LocalDate.of(2026, 2, 15), "회사 근처 식당");

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        given(expenseRepository.save(any(Expense.class))).willAnswer(invocation -> {
            Expense saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 1L);
            ReflectionTestUtils.setField(saved, "createdAt", LocalDateTime.of(2026, 2, 15, 12, 0));
            ReflectionTestUtils.setField(saved, "updatedAt", LocalDateTime.of(2026, 2, 15, 12, 0));
            return saved;
        });

        // when
        ExpenseResponse response = expenseService.createExpense(1L, request);

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.amount()).isEqualTo(15000L);
        assertThat(response.description()).isEqualTo("점심 식사");
        assertThat(response.expenseDate()).isEqualTo(LocalDate.of(2026, 2, 15));
        assertThat(response.memo()).isEqualTo("회사 근처 식당");
        assertThat(response.category().id()).isEqualTo(1L);
        assertThat(response.category().name()).isEqualTo("식비");

        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    @DisplayName("createExpense_존재하지않는유저_예외발생")
    void createExpense_존재하지않는유저_예외발생() {
        // given
        ExpenseCreateRequest request = new ExpenseCreateRequest(1L, 15000L, "점심 식사", LocalDate.of(2026, 2, 15), null);

        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> expenseService.createExpense(999L, request))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("createExpense_존재하지않는카테고리_예외발생")
    void createExpense_존재하지않는카테고리_예외발생() {
        // given
        User user = createTestUser();
        ExpenseCreateRequest request = new ExpenseCreateRequest(999L, 15000L, "점심 식사", LocalDate.of(2026, 2, 15), null);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(categoryRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> expenseService.createExpense(1L, request))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND);
                });
    }

    // ===== getExpenses =====

    @Test
    @DisplayName("getExpenses_월별조회_페이지결과반환")
    void getExpenses_월별조회_페이지결과반환() {
        // given
        User user = createTestUser();
        Category category = createTestCategory();
        Expense expense = createTestExpense(user, category);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Expense> expensePage = new PageImpl<>(List.of(expense), pageable, 1);

        LocalDate startDate = LocalDate.of(2026, 2, 1);
        LocalDate endDate = LocalDate.of(2026, 2, 28);

        given(expenseRepository.findByUserIdAndExpenseDateBetween(1L, startDate, endDate, pageable))
                .willReturn(expensePage);

        // when
        Page<ExpenseResponse> result = expenseService.getExpenses(1L, 2026, 2, null, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

        ExpenseResponse response = result.getContent().get(0);
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.amount()).isEqualTo(15000L);
        assertThat(response.description()).isEqualTo("점심 식사");
    }

    @Test
    @DisplayName("getExpenses_카테고리필터_필터된결과반환")
    void getExpenses_카테고리필터_필터된결과반환() {
        // given
        User user = createTestUser();
        Category category = createTestCategory();
        Expense expense = createTestExpense(user, category);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Expense> expensePage = new PageImpl<>(List.of(expense), pageable, 1);

        LocalDate startDate = LocalDate.of(2026, 2, 1);
        LocalDate endDate = LocalDate.of(2026, 2, 28);

        given(expenseRepository.findByUserIdAndExpenseDateBetweenAndCategoryId(1L, startDate, endDate, 1L, pageable))
                .willReturn(expensePage);

        // when
        Page<ExpenseResponse> result = expenseService.getExpenses(1L, 2026, 2, 1L, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

        ExpenseResponse response = result.getContent().get(0);
        assertThat(response.category().id()).isEqualTo(1L);
        assertThat(response.category().name()).isEqualTo("식비");
    }

    // ===== getExpense =====

    @Test
    @DisplayName("getExpense_정상조회_지출상세반환")
    void getExpense_정상조회_지출상세반환() {
        // given
        User user = createTestUser();
        Category category = createTestCategory();
        Expense expense = createTestExpense(user, category);

        given(expenseRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(expense));

        // when
        ExpenseResponse response = expenseService.getExpense(1L, 1L);

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.amount()).isEqualTo(15000L);
        assertThat(response.description()).isEqualTo("점심 식사");
        assertThat(response.expenseDate()).isEqualTo(LocalDate.of(2026, 2, 15));
        assertThat(response.memo()).isEqualTo("회사 근처 식당");
        assertThat(response.category().id()).isEqualTo(1L);
        assertThat(response.category().name()).isEqualTo("식비");
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.updatedAt()).isNotNull();
    }

    @Test
    @DisplayName("getExpense_존재하지않는지출_예외발생")
    void getExpense_존재하지않는지출_예외발생() {
        // given
        given(expenseRepository.findByIdAndUserId(999L, 1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> expenseService.getExpense(1L, 999L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.EXPENSE_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("getExpense_타인의지출조회_예외발생")
    void getExpense_타인의지출조회_예외발생() {
        // given
        // findByIdAndUserId returns empty when userId doesn't match the expense owner
        given(expenseRepository.findByIdAndUserId(1L, 999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> expenseService.getExpense(999L, 1L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.EXPENSE_NOT_FOUND);
                });
    }

    // ===== updateExpense =====

    @Test
    @DisplayName("updateExpense_정상수정_수정된지출반환")
    void updateExpense_정상수정_수정된지출반환() {
        // given
        User user = createTestUser();
        Category category = createTestCategory();
        Expense expense = createTestExpense(user, category);

        Category newCategory = Category.createDefault("교통");
        ReflectionTestUtils.setField(newCategory, "id", 2L);

        ExpenseUpdateRequest request = new ExpenseUpdateRequest(2L, 3000L, "버스비", LocalDate.of(2026, 2, 20), "출퇴근");

        given(expenseRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(expense));
        given(categoryRepository.findById(2L)).willReturn(Optional.of(newCategory));

        // when
        ExpenseResponse response = expenseService.updateExpense(1L, 1L, request);

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.amount()).isEqualTo(3000L);
        assertThat(response.description()).isEqualTo("버스비");
        assertThat(response.expenseDate()).isEqualTo(LocalDate.of(2026, 2, 20));
        assertThat(response.memo()).isEqualTo("출퇴근");
        assertThat(response.category().id()).isEqualTo(2L);
        assertThat(response.category().name()).isEqualTo("교통");
    }

    @Test
    @DisplayName("updateExpense_존재하지않는지출_예외발생")
    void updateExpense_존재하지않는지출_예외발생() {
        // given
        ExpenseUpdateRequest request = new ExpenseUpdateRequest(1L, 3000L, "버스비", LocalDate.of(2026, 2, 20), null);

        given(expenseRepository.findByIdAndUserId(999L, 1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> expenseService.updateExpense(1L, 999L, request))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.EXPENSE_NOT_FOUND);
                });
    }

    // ===== deleteExpense =====

    @Test
    @DisplayName("deleteExpense_정상삭제_성공")
    void deleteExpense_정상삭제_성공() {
        // given
        User user = createTestUser();
        Category category = createTestCategory();
        Expense expense = createTestExpense(user, category);

        given(expenseRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(expense));

        // when
        expenseService.deleteExpense(1L, 1L);

        // then
        verify(expenseRepository).delete(expense);
    }

    @Test
    @DisplayName("deleteExpense_존재하지않는지출_예외발생")
    void deleteExpense_존재하지않는지출_예외발생() {
        // given
        given(expenseRepository.findByIdAndUserId(999L, 1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> expenseService.deleteExpense(1L, 999L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.EXPENSE_NOT_FOUND);
                });
    }
}
