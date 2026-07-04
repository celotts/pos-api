package com.posapi.application.service.category;

import com.posapi.application.port.category.CategoryManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.category.Category;
import com.posapi.domain.port.output.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryManagementPort {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new DuplicateResourceException("Category with name '" + category.getName() + "' already exists.");
        }
        category.setId(UUID.randomUUID());
        
        // Guardamos la entidad y la devolvemos inmediatamente.
        // El adaptador de persistencia se asegurará de que la entidad guardada se mapee de nuevo al dominio,
        // trayendo consigo los valores generados por la base de datos como createdAt.
        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(UUID id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<Category> updateCategory(UUID id, Category category) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    if (category.getName() != null && !category.getName().equals(existingCategory.getName())) {
                        if (categoryRepository.existsByName(category.getName())) {
                            throw new DuplicateResourceException(
                                    "Category with name '" + category.getName() + "' already exists.");
                        }
                        existingCategory.setName(category.getName());
                    }
                    // Al guardar, @UpdateTimestamp se activará y actualizará el updatedAt.
                    // El adaptador devolverá el objeto de dominio con el timestamp actualizado.
                    return categoryRepository.save(existingCategory);
                });
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }
}
