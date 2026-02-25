package com.budget.api.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 내부 오류가 발생했습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C003", "접근 권한이 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C004", "인증이 필요합니다."),

    // Auth
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A001", "이메일 또는 비밀번호가 올바르지 않습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "만료된 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "유효하지 않은 토큰입니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U002", "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U003", "비밀번호가 올바르지 않습니다."),

    // Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CA001", "카테고리를 찾을 수 없습니다."),

    // Salary
    SALARY_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "급여 정보를 찾을 수 없습니다."),
    DUPLICATE_SALARY(HttpStatus.CONFLICT, "S002", "해당 월의 급여가 이미 등록되어 있습니다."),

    // Expense
    EXPENSE_NOT_FOUND(HttpStatus.NOT_FOUND, "E001", "지출 내역을 찾을 수 없습니다."),

    // Budget
    BUDGET_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "예산 정보를 찾을 수 없습니다."),
    DUPLICATE_BUDGET(HttpStatus.CONFLICT, "B002", "해당 카테고리의 월별 예산이 이미 설정되어 있습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
