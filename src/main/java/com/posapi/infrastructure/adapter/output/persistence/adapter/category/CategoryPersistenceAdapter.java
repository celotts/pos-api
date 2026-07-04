package com.posapi.infrastructure.adapter.output.persistence.adapter.category;

import com.posapi.domain.model.category.Category;
import com.posapi.domain.port.output.CategoryRepository;
import com.posapi.infrastructure.adapter.output.persistence.entity.category.CategoryEntity;
import com.posapi.infrastructure.adapter.output.persistence.mapper.category.CategoryPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.category.CategoryJpaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryPersistenceMapper categoryMapper;
    private final EntityManager entityManager;

    @Override
    public Category save(Category category) {
        CategoryEntity entity = categoryMapper.toEntity(category);
        
        // 1. Guardar y forzar la sincronización con la base de datos
        CategoryEntity savedEntity = categoryJpaRepository.saveAndFlush(entity);
        
        // 2. Refrescar la entidad desde la base de datos para obtener los valores generados
        entityManager.refresh(savedEntity);
        
        // 3. Mapear la entidad refrescada de vuelta al dominio
        return categoryMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return categoryJpaRepository.findById(id).map(categoryMapper::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return categoryJpaRepository.findAll().stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        categoryJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryJpaRepository.existsByName(name);
    }
}
