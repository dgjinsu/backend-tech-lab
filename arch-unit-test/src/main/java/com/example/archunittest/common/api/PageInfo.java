package com.example.archunittest.common.api;

import org.springframework.data.domain.Page;

public record PageInfo (
    int currentPage,
    int pageSize,
    long totalItems,
    int totalPages,
    boolean isFirst,
    boolean isLast
) {

    public static PageInfo from(Page<?> page) {
        return new PageInfo(
            page.getNumber() + 1, // 페이지 번호는 0부터 시작하므로 1을 더함
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }

}
