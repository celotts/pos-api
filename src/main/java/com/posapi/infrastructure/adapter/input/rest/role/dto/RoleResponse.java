package com.posapi.infrastructure.adapter.input.rest.role.dto;

import com.posapi.domain.model.role.Role;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record RoleResponse(
    UUID id,
    String name,
    Instant createdAt,
    Instant updatedAt,
    String createdByName,
    String updatedByName
) {
    public static RoleResponse fromDomain(Role role, String createdByName, String updatedByName) {
        return new RoleResponse(
            role.getId(),
            role.getName(),
            role.getCreatedAt(),
            role.getUpdatedAt(),
            createdByName,
            updatedByName
        );
    }
}
