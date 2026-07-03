package com.posapi.application.port.category;

import com.posapi.domain.model.category.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryInputPort {

    Category createCategory(Category category);
    Optional<Category> getCategoryById(UUID id);
    List<Category> getAllCategories();
    Category updateCategory(UUID id, Category updateCategory);
    void deleteCategory(UUID id);
    Optional<Category> getCategoryByName(String name);
}
