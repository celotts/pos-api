package com.posapi.infrastructure.adapter.output.persistence.mapper.category;

import com.posapi.domain.model.category.Category;
import com.posapi.infrastructure.adapter.output.persistence.entity.category.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryPersistenceMapper {

    public CategoryEntity toEntity(Category domain) {
        return CategoryEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public Category toDomain(CategoryEntity entity) {
        return Category.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
