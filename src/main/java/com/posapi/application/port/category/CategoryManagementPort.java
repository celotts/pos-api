package com.posapi.application.port.category;

import com.posapi.domain.model.category.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryManagementPort {
    Category createCategory(Category category);
    Optional<Category> getCategoryById(UUID id);
    List<Category> getAllCategories();
    Optional<Category> updateCategory(UUID id, Category category);
    void deleteCategory(UUID id);
}
