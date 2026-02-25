package com.budget.api.domain.category.repository;

import com.budget.api.domain.category.entity.Category;
import com.budget.api.domain.category.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByType(CategoryType type);

    List<Category> findByUserIdOrType(Long userId, CategoryType type);
}
