package com.example.abac.domain.expense;

import com.example.abac.domain.expense.dto.CreateExpenseRequest;
import com.example.abac.domain.expense.dto.ExpenseResponse;
import com.example.abac.domain.expense.dto.UpdateExpenseRequest;
import com.example.abac.security.CustomUserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// [학습 포인트] 이 서비스의 책임은 두 가지:
//  1) 상태 전이 가드 (assertStatus) — Policy가 통과시킨 뒤에도 '현재 상태'에서 갈 수 있는 다음 상태만 허용
//  2) 데이터 스코프 적용 (findById/findAll) — Specification으로 '볼 수 있는 범위' WHERE 주입
// 클래스 레벨 readOnly 트랜잭션에 쓰기 메서드만 @Transactional로 덮어쓴다.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Transactional
    public ExpenseResponse create(CreateExpenseRequest request, CustomUserPrincipal principal) {
        // ownerId/departmentId는 Principal에서만. 요청 바디가 아무리 꾸며져도 여기서 덮인다.
        Expense expense = new Expense(
                principal.getUserId(),
                principal.getDepartmentId(),
                request.amount(),
                request.description()
        );
        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponse update(Long id, UpdateExpenseRequest request) {
        Expense expense = requireById(id);
        // Policy에서 canEditDraft가 이미 본인+DRAFT를 확인했지만, Service에서도 한 번 더 가드.
        // 이유: Policy가 우회되거나 Service가 다른 경로에서 호출돼도 도메인 불변을 지키기 위함.
        if (!expense.isDraft()) {
            throw new IllegalStateException("Only DRAFT expenses can be edited. current=" + expense.getStatus());
        }
        expense.updateDraft(request.amount(), request.description());
        return ExpenseResponse.from(expense);
    }

    @Transactional
    public void delete(Long id) {
        Expense expense = requireById(id);
        if (!expense.isDraft()) {
            throw new IllegalStateException("Only DRAFT expenses can be deleted. current=" + expense.getStatus());
        }
        expenseRepository.delete(expense);
    }

    @Transactional
    public ExpenseResponse submit(Long id) {
        Expense expense = requireById(id);
        assertStatus(expense, ExpenseStatus.DRAFT, ExpenseStatus.SUBMITTED);
        expense.changeStatus(ExpenseStatus.SUBMITTED);
        return ExpenseResponse.from(expense);
    }

    @Transactional
    public ExpenseResponse approve(Long id) {
        Expense expense = requireById(id);
        assertStatus(expense, ExpenseStatus.SUBMITTED, ExpenseStatus.APPROVED);
        expense.changeStatus(ExpenseStatus.APPROVED);
        return ExpenseResponse.from(expense);
    }

    @Transactional
    public ExpenseResponse reject(Long id) {
        Expense expense = requireById(id);
        assertStatus(expense, ExpenseStatus.SUBMITTED, ExpenseStatus.REJECTED);
        expense.changeStatus(ExpenseStatus.REJECTED);
        return ExpenseResponse.from(expense);
    }

    @Transactional
    public ExpenseResponse pay(Long id) {
        Expense expense = requireById(id);
        assertStatus(expense, ExpenseStatus.APPROVED, ExpenseStatus.PAID);
        expense.changeStatus(ExpenseStatus.PAID);
        return ExpenseResponse.from(expense);
    }

    // [상태 전이 가드] 현재 상태가 expected가 아닐 때 IllegalStateException → 409 Conflict.
    // 동시성/중복 호출(이미 approve된 건 다시 approve) 시에도 여기서 튕긴다.
    private static void assertStatus(Expense expense, ExpenseStatus expected, ExpenseStatus next) {
        if (expense.getStatus() != expected) {
            throw new IllegalStateException(
                    "Invalid transition: " + expense.getStatus() + " -> " + next
                            + " (expected current=" + expected + ")"
            );
        }
    }

    // [데이터 스코프 조립] visibleTo(역할별 WHERE) AND hasId(단건 필터).
    // 타인의 건을 id로 찍어도 WHERE에 걸려 빈 결과 → 404. '타인 건이 있다'는 정보조차 새지 않음.
    public ExpenseResponse findById(Long id, CustomUserPrincipal principal) {
        Specification<Expense> spec = ExpenseSpecifications.visibleTo(principal)
                .and(ExpenseSpecifications.hasId(id));
        return expenseRepository.findOne(spec)
                .map(ExpenseResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found: " + id));
    }

    // 목록도 같은 Specification으로 역할별 자동 필터 → 페이지네이션은 Pageable이 처리.
    public Page<ExpenseResponse> findAll(CustomUserPrincipal principal, Pageable pageable) {
        return expenseRepository.findAll(ExpenseSpecifications.visibleTo(principal), pageable)
                .map(ExpenseResponse::from);
    }

    // 상태 전이 메서드 전용 내부 조회 — 스코프 체크 없이 단순 ID 조회.
    // 액션 권한은 Policy에서 이미 걸러졌기 때문에 여기서는 존재 여부만 본다.
    Expense requireById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found: " + id));
    }
}
