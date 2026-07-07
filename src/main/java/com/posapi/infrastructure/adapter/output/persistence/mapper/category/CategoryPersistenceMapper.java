package com.posapi.infrastructure.adapter.output.persistence.mapper.category;

import com.posapi.domain.model.category.Category;
import com.posapi.infrastructure.adapter.output.persistence.entity.category.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryPersistenceMapper {

    public CategoryEntity toEntity(Category domain) {
        if (domain == null) return null;
        return CategoryEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .createdBy(domain.getCreatedBy()) // <-- AÑADIDO
                .updatedBy(domain.getUpdatedBy()) // <-- AÑADIDO
                .build();
    }

    public Category toDomain(CategoryEntity entity) {
        if (entity == null) return null;
        return Category.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy()) // <-- AÑADIDO
                .updatedBy(entity.getUpdatedBy()) // <-- AÑADIDO
                .build();
    }
}
