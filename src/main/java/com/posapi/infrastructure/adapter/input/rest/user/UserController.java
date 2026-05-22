package com.posapi.infrastructure.adapter.input.rest.user;

import com.posapi.application.port.user.UserManagementPort;
import com.posapi.domain.model.user.User;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserRequest;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserManagementPort userManagementPort;

    public UserController(UserManagementPort userManagementPort) {
        this.userManagementPort = userManagementPort;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest userRequest) {
        User user = User.builder()
                .email(userRequest.getEmail())
                .passwordHash(userRequest.getPassword()) // La contraseña se codificará en UserService
                .fullName(userRequest.getFullName())
                .isActive(true) // Por defecto activo
                .role("USER") // Por defecto rol USER, un ADMIN podría cambiarlo después
                .build();

        User createdUser = userManagementPort.createUser(user);
        return new ResponseEntity<>(UserResponse.fromUser(createdUser), HttpStatus.CREATED); // Usar fromUser
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return userManagementPort.getUserById(id)
                .map(UserResponse::fromUser) // Usar fromUser
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return userManagementPort.getUserByEmail(email)
                .map(UserResponse::fromUser) // Usar fromUser
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
