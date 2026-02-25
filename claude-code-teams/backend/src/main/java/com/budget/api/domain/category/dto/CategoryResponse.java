package com.budget.api.domain.category.dto;

import com.budget.api.domain.category.entity.Category;
import com.budget.api.domain.category.entity.CategoryType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {

    private Long id;
    private String name;
    private CategoryType type;

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .build();
    }
}
