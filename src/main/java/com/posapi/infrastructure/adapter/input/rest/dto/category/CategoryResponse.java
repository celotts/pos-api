package com.posapi.infrastructure.adapter.input.rest.dto.category;

import com.posapi.domain.model.category.Category;

import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
    UUID id,
    String name,
    Instant createdAt,
    Instant updatedAt
) {
    public static CategoryResponse fromDomain(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }
}
