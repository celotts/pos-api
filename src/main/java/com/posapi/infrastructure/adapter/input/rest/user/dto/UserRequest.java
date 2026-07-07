package com.posapi.infrastructure.adapter.input.rest.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserRequest(
    @NotBlank @Email
    String email,

    @NotBlank @Size(min = 8)
    String password,

    @NotBlank
    String fullName,

    @NotNull
    UUID roleId,

    Boolean isActive
) {
}
