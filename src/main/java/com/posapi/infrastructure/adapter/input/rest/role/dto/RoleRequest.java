package com.posapi.infrastructure.adapter.input.rest.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleRequest(
        @NotBlank(message = "Role name cannot be blank")
        @Size(min = 2, max = 50, message = "Role name must be between {min} and {max} characters")
        String name
) {
    // Al ser un record, Java genera automáticamente el método name() por debajo.
    // No necesitas declarar getters manualmente.
}
