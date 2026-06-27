package com.posapi.application.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 
 * 
 * DTO for authentication requests.
 */
public record AuthenticationRequest(
        @NotBlank(message = "Username cannot be blank") @Email(message = "Username must be a valid email format") String username,
        @NotBlank(message = "Password cannot be blank") @Size(min = 6, message = "Password must be at least 6 characters long") String password) {
}
