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
                .deletedAt(domain.getDeletedAt()) // Añadido
                .createdByUserId(domain.getCreatedByUserId()) // Corregido
                .updatedByUserId(domain.getUpdatedByUserId()) // Corregido
                .deletedByUserId(domain.getDeletedByUserId()) // Añadido
                .createdByRoleId(domain.getCreatedByRoleId()) // Añadido
                .updatedByRoleId(domain.getUpdatedByRoleId()) // Añadido
                .deletedByRoleId(domain.getDeletedByRoleId()) // Añadido
                .build();
    }

    public Category toDomain(CategoryEntity entity) {
        if (entity == null) return null;
        return Category.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt()) // Añadido
                .createdByUserId(entity.getCreatedByUserId()) // Corregido
                .updatedByUserId(entity.getUpdatedByUserId()) // Corregido
                .deletedByUserId(entity.getDeletedByUserId()) // Añadido
                .createdByRoleId(entity.getCreatedByRoleId()) // Añadido
                .updatedByRoleId(entity.getUpdatedByRoleId()) // Añadido
                .deletedByRoleId(entity.getDeletedByRoleId()) // Añadido
                .build();
    }
}
