package com.budget.api.domain.salary.service;

import com.budget.api.domain.salary.dto.FixedExpenseRequest;
import com.budget.api.domain.salary.dto.SalaryCreateRequest;
import com.budget.api.domain.salary.dto.SalaryListResponse;
import com.budget.api.domain.salary.dto.SalaryResponse;
import com.budget.api.domain.salary.dto.SalaryUpdateRequest;
import com.budget.api.domain.salary.entity.FixedExpense;
import com.budget.api.domain.salary.entity.Salary;
import com.budget.api.domain.salary.repository.SalaryRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SalaryServiceTest {

    @InjectMocks
    private SalaryService salaryService;

    @Mock
    private SalaryRepository salaryRepository;

    @Mock
    private UserRepository userRepository;

    private User createTestUser() {
        User user = User.create("test@example.com", "encodedPassword", "테스터");
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private User createOtherUser() {
        User user = User.create("other@example.com", "encodedPassword", "다른사용자");
        ReflectionTestUtils.setField(user, "id", 2L);
        return user;
    }

    private Salary createTestSalary(User user) {
        Salary salary = Salary.create(user, 5000000L, 2026, 2, "2월 급여");
        ReflectionTestUtils.setField(salary, "id", 1L);
        return salary;
    }

    private Salary createTestSalaryWithFixedExpenses(User user) {
        Salary salary = createTestSalary(user);

        FixedExpense rent = FixedExpense.builder().name("월세").amount(500000L).build();
        FixedExpense insurance = FixedExpense.builder().name("보험료").amount(200000L).build();
        salary.addFixedExpense(rent);
        salary.addFixedExpense(insurance);

        return salary;
    }

    @Test
    @DisplayName("createSalary_성공_급여와고정지출함께생성")
    void createSalary_성공_급여와고정지출함께생성() {
        // given
        User user = createTestUser();
        List<FixedExpenseRequest> fixedExpenses = List.of(
                new FixedExpenseRequest("월세", 500000L),
                new FixedExpenseRequest("보험료", 200000L)
        );
        SalaryCreateRequest request = new SalaryCreateRequest(2026, 2, 5000000L, fixedExpenses, "2월 급여");

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(salaryRepository.findByUserIdAndYearAndMonth(1L, 2026, 2)).willReturn(Optional.empty());
        given(salaryRepository.save(any(Salary.class))).willAnswer(invocation -> {
            Salary saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 1L);
            return saved;
        });

        // when
        SalaryResponse response = salaryService.createSalary(1L, request);

        // then
        assertThat(response.year()).isEqualTo(2026);
        assertThat(response.month()).isEqualTo(2);
        assertThat(response.totalAmount()).isEqualTo(5000000L);
        assertThat(response.memo()).isEqualTo("2월 급여");
        assertThat(response.fixedExpenses()).hasSize(2);
        assertThat(response.fixedExpenseTotal()).isEqualTo(700000L);
        assertThat(response.availableAmount()).isEqualTo(4300000L);

        verify(salaryRepository).save(any(Salary.class));
    }

    @Test
    @DisplayName("createSalary_실패_해당월급여중복")
    void createSalary_실패_해당월급여중복() {
        // given
        User user = createTestUser();
        Salary existingSalary = createTestSalary(user);
        SalaryCreateRequest request = new SalaryCreateRequest(2026, 2, 5000000L, null, "2월 급여");

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(salaryRepository.findByUserIdAndYearAndMonth(1L, 2026, 2)).willReturn(Optional.of(existingSalary));

        // when & then
        assertThatThrownBy(() -> salaryService.createSalary(1L, request))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_SALARY);
                });
    }

    @Test
    @DisplayName("createSalary_실패_존재하지않는사용자")
    void createSalary_실패_존재하지않는사용자() {
        // given
        SalaryCreateRequest request = new SalaryCreateRequest(2026, 2, 5000000L, null, "2월 급여");

        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> salaryService.createSalary(999L, request))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("createSalary_성공_고정지출없이급여만생성")
    void createSalary_성공_고정지출없이급여만생성() {
        // given
        User user = createTestUser();
        SalaryCreateRequest request = new SalaryCreateRequest(2026, 3, 4500000L, null, "3월 급여");

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(salaryRepository.findByUserIdAndYearAndMonth(1L, 2026, 3)).willReturn(Optional.empty());
        given(salaryRepository.save(any(Salary.class))).willAnswer(invocation -> {
            Salary saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 2L);
            return saved;
        });

        // when
        SalaryResponse response = salaryService.createSalary(1L, request);

        // then
        assertThat(response.totalAmount()).isEqualTo(4500000L);
        assertThat(response.fixedExpenses()).isEmpty();
        assertThat(response.fixedExpenseTotal()).isEqualTo(0L);
        assertThat(response.availableAmount()).isEqualTo(4500000L);
    }

    @Test
    @DisplayName("getSalaries_성공_연도별목록조회")
    void getSalaries_성공_연도별목록조회() {
        // given
        User user = createTestUser();

        Salary jan = Salary.create(user, 5000000L, 2026, 1, "1월 급여");
        ReflectionTestUtils.setField(jan, "id", 1L);

        Salary feb = Salary.create(user, 5200000L, 2026, 2, "2월 급여");
        ReflectionTestUtils.setField(feb, "id", 2L);

        given(salaryRepository.findByUserIdAndYear(1L, 2026)).willReturn(List.of(jan, feb));

        // when
        List<SalaryListResponse> responses = salaryService.getSalaries(1L, 2026);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).year()).isEqualTo(2026);
        assertThat(responses.get(0).month()).isEqualTo(1);
        assertThat(responses.get(0).totalAmount()).isEqualTo(5000000L);
        assertThat(responses.get(1).month()).isEqualTo(2);
        assertThat(responses.get(1).totalAmount()).isEqualTo(5200000L);
    }

    @Test
    @DisplayName("getSalaries_성공_연도미지정시현재연도조회")
    void getSalaries_성공_연도미지정시현재연도조회() {
        // given
        given(salaryRepository.findByUserIdAndYear(eq(1L), any(Integer.class))).willReturn(List.of());

        // when
        List<SalaryListResponse> responses = salaryService.getSalaries(1L, null);

        // then
        assertThat(responses).isEmpty();
        verify(salaryRepository).findByUserIdAndYear(eq(1L), any(Integer.class));
    }

    @Test
    @DisplayName("getSalary_성공_상세조회")
    void getSalary_성공_상세조회() {
        // given
        User user = createTestUser();
        Salary salary = createTestSalaryWithFixedExpenses(user);

        given(salaryRepository.findById(1L)).willReturn(Optional.of(salary));

        // when
        SalaryResponse response = salaryService.getSalary(1L, 1L);

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.year()).isEqualTo(2026);
        assertThat(response.month()).isEqualTo(2);
        assertThat(response.totalAmount()).isEqualTo(5000000L);
        assertThat(response.fixedExpenses()).hasSize(2);
        assertThat(response.fixedExpenseTotal()).isEqualTo(700000L);
        assertThat(response.availableAmount()).isEqualTo(4300000L);
        assertThat(response.memo()).isEqualTo("2월 급여");
    }

    @Test
    @DisplayName("getSalary_실패_존재하지않는급여")
    void getSalary_실패_존재하지않는급여() {
        // given
        given(salaryRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> salaryService.getSalary(1L, 999L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.SALARY_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("getSalary_실패_타인의급여조회시도")
    void getSalary_실패_타인의급여조회시도() {
        // given
        User owner = createTestUser();
        Salary salary = createTestSalary(owner);

        given(salaryRepository.findById(1L)).willReturn(Optional.of(salary));

        // when & then
        assertThatThrownBy(() -> salaryService.getSalary(2L, 1L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
                });
    }

    @Test
    @DisplayName("updateSalary_성공_급여수정")
    void updateSalary_성공_급여수정() {
        // given
        User user = createTestUser();
        Salary salary = createTestSalary(user);
        SalaryUpdateRequest request = new SalaryUpdateRequest(5500000L, null, "수정된 메모");

        given(salaryRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(salary));

        // when
        SalaryResponse response = salaryService.updateSalary(1L, 1L, request);

        // then
        assertThat(response.totalAmount()).isEqualTo(5500000L);
        assertThat(response.memo()).isEqualTo("수정된 메모");
    }

    @Test
    @DisplayName("updateSalary_성공_고정지출변경")
    void updateSalary_성공_고정지출변경() {
        // given
        User user = createTestUser();
        Salary salary = createTestSalaryWithFixedExpenses(user);
        List<FixedExpenseRequest> newFixedExpenses = List.of(
                new FixedExpenseRequest("월세", 600000L),
                new FixedExpenseRequest("통신비", 100000L),
                new FixedExpenseRequest("교통비", 150000L)
        );
        SalaryUpdateRequest request = new SalaryUpdateRequest(5000000L, newFixedExpenses, "고정지출 변경");

        given(salaryRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(salary));

        // when
        SalaryResponse response = salaryService.updateSalary(1L, 1L, request);

        // then
        assertThat(response.totalAmount()).isEqualTo(5000000L);
        assertThat(response.fixedExpenses()).hasSize(3);
        assertThat(response.fixedExpenseTotal()).isEqualTo(850000L);
        assertThat(response.availableAmount()).isEqualTo(4150000L);
        assertThat(response.memo()).isEqualTo("고정지출 변경");
    }

    @Test
    @DisplayName("updateSalary_실패_존재하지않는급여")
    void updateSalary_실패_존재하지않는급여() {
        // given
        SalaryUpdateRequest request = new SalaryUpdateRequest(5500000L, null, "메모");

        given(salaryRepository.findByIdAndUserId(999L, 1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> salaryService.updateSalary(1L, 999L, request))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.SALARY_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("deleteSalary_성공_급여삭제")
    void deleteSalary_성공_급여삭제() {
        // given
        User user = createTestUser();
        Salary salary = createTestSalary(user);

        given(salaryRepository.findByIdAndUserId(1L, 1L)).willReturn(Optional.of(salary));

        // when
        salaryService.deleteSalary(1L, 1L);

        // then
        verify(salaryRepository).delete(salary);
    }

    @Test
    @DisplayName("deleteSalary_실패_타인의급여삭제시도")
    void deleteSalary_실패_타인의급여삭제시도() {
        // given
        given(salaryRepository.findByIdAndUserId(1L, 2L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> salaryService.deleteSalary(2L, 1L))
                .isInstanceOf(CustomException.class)
                .satisfies(exception -> {
                    CustomException customException = (CustomException) exception;
                    assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.SALARY_NOT_FOUND);
                });
    }
}
