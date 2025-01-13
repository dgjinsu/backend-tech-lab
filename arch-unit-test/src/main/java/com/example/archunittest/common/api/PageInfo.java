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
            page.getNumber() + 1,
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }
}
