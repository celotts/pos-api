package com.posapi.infrastructure.adapter.input.rest.category.mapper;

import com.posapi.domain.model.category.Category;
import com.posapi.infrastructure.adapter.input.rest.category.dto.CategoryRequest;
import com.posapi.infrastructure.adapter.input.rest.category.dto.CategoryResponse;

public class CategoryRestMapper {

    public Category toDomain(CategoryRequest request, java.util.UUID currentUserId, java.util.UUID currentUserRoleId) {
        if (request == null)
            return null;

        return Category.createNew(
                request.name(),
                currentUserId,
                currentUserRoleId
        );
    }

    public CategoryResponse toResponse(Category category, String createdByName, String updatedByName, String deletedByName) {
        if (category == null) return null;

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                category.getDeletedAt(),
                category.getCreatedByUserId(),
                category.getUpdatedByUserId(),
                category.getDeletedByUserId(),
                createdByName,
                updatedByName,
                deletedByName

            );
        }
}

