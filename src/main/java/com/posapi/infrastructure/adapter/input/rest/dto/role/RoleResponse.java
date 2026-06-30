package com.posapi.infrastructure.adapter.input.rest.dto.role;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posapi.domain.model.role.Role;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoleResponse(
        // --- Standard Fields ---
        UUID id,
        Instant createdAt,
        Instant updatedAt,

        // --- Role-Specific Fields ---
        String name,
        UUID createdBy,
        String createdByName,
        UUID updatedBy,
        String updatedByName
) {
    public static RoleResponse fromDomain(Role role, String createdByName, String updatedByName) {
        return new RoleResponse(
                role.getId(),
                role.getCreatedAt(),
                role.getUpdatedAt(),
                role.getName(),
                role.getCreatedBy(),
                createdByName,
                role.getUpdatedBy(),
                updatedByName
        );
    }
}
