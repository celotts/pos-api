package com.posapi.infrastructure.adapter.output.persistence.adapter.category;

import com.posapi.domain.model.category.Category;
import com.posapi.domain.port.output.CategoryRepository;
import com.posapi.infrastructure.adapter.output.persistence.entity.category.CategoryEntity;
import com.posapi.infrastructure.adapter.output.persistence.mapper.category.CategoryPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.category.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryPersistenceMapper categoryMapper;

    @Override
    public Category save(Category category) {
        CategoryEntity entity = categoryMapper.toEntity(category);
        CategoryEntity savedEntity = categoryJpaRepository.save(entity);
        return categoryMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return categoryJpaRepository.findById(id).map(categoryMapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return categoryJpaRepository.existsById(id);
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        // CORREGIDO: Usar el método map de Page para transformar Page<CategoryEntity> a Page<Category>
        return categoryJpaRepository.findAll(pageable)
                .map(categoryMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        categoryJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryJpaRepository.existsByName(name);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return categoryJpaRepository.findByName(name).map(categoryMapper::toDomain);
    }
}
