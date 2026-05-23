package com.posapi.infrastructure.adapter.input.rest.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password; // Contraseña en texto plano para la entrada

    @NotBlank(message = "Full name cannot be empty")
    private String fullName;

    // El rol y el estado activo se pueden manejar por defecto o con lógica de negocio
    // private String role;
    // private Boolean isActive;
}
