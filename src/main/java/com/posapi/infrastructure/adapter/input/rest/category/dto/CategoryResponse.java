package com.posapi.infrastructure.adapter.input.rest.category.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posapi.domain.model.category.Category;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryResponse(
    UUID id,
    String name,
    Instant createdAt,
    Instant updatedAt,
    String createdByName,
    String updatedByName
) {
    public static CategoryResponse fromDomain(Category category, String createdByName, String updatedByName) {
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getCreatedAt(),
            category.getUpdatedAt(),
            createdByName,
            updatedByName
        );
    }
}
