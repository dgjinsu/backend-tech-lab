package com.budget.api.domain.category.service;

import com.budget.api.domain.category.dto.CategoryCreateRequest;
import com.budget.api.domain.category.dto.CategoryResponse;
import com.budget.api.domain.category.entity.Category;
import com.budget.api.domain.category.entity.CategoryType;
import com.budget.api.domain.category.repository.CategoryRepository;
import com.budget.api.domain.user.entity.User;
import com.budget.api.domain.user.repository.UserRepository;
import com.budget.api.global.exception.CustomException;
import com.budget.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public List<CategoryResponse> getCategories(Long userId) {
        List<Category> categories = categoryRepository.findByUserIdOrType(userId, CategoryType.DEFAULT);
        return categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse createCategory(Long userId, CategoryCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Category category = Category.createCustom(request.getName(), user);
        Category savedCategory = categoryRepository.save(category);
        return CategoryResponse.from(savedCategory);
    }
}
