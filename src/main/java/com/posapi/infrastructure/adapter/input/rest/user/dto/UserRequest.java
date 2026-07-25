package com.posapi.infrastructure.adapter.input.rest.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern; // AÑADIDO
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

    @NotBlank
    String address,

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone number must be between 7 and 15 digits and can start with '+'") // AÑADIDO
    String phone,

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone2 number must be between 7 and 15 digits and can start with '+'") // AÑADIDO
    String phone2,

    @NotNull
    UUID roleId,

    Boolean isActive
) {
}
