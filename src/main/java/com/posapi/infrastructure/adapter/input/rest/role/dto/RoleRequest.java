package com.posapi.infrastructure.adapter.input.rest.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleRequest(
    @NotBlank(message = "Role name cannot be blank")
    @Size(min = 2, max = 50)
    String name
) {
}
