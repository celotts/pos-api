package com.posapi.application.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationRequest {
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, message = "Username must have at least 3 characters")
    private String username;

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
    private String email;

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, message = "Password must have at least 6 characters")
    private String password;

    // Constructors
    public UserRegistrationRequest() {}

    public UserRegistrationRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
