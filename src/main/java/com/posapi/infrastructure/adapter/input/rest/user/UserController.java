package com.posapi.infrastructure.adapter.input.rest.user;

import com.posapi.application.port.user.UserManagementPort;
import com.posapi.domain.model.user.User;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserRequest;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserManagementPort userManagementPort;

    @Value("${app.roles.USER:USER}")
    private String defaultUserRole;

    @Value("${app.user.default.active:true}")
    private boolean defaultUserActiveStatus;

    @Value("${app.roles.ADMIN:ADMIN}")
    private String adminRole;

    @Value("${app.user.admin-creation.active:true}")
    private boolean adminActiveStatus;

    public UserController(UserManagementPort userManagementPort) {
        this.userManagementPort = userManagementPort;
    }

    @PostMapping("/register")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest userRequest) {
        return createUserWorkflow(userRequest, defaultUserRole, defaultUserActiveStatus);
    }

    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserResponse> registerAdmin(@Valid @RequestBody UserRequest userRequest) {
        return createUserWorkflow(userRequest, adminRole, adminActiveStatus);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return userManagementPort.getUserById(id)
                .map(UserResponse::fromUser)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return userManagementPort.getUserByEmail(email)
                .map(UserResponse::fromUser)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userManagementPort.getAllUsers().stream()
                .map(UserResponse::fromUser)
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.username == #userRequest.email")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UserRequest userRequest) {
        // El controlador solo mapea lo que el cliente envía hacia el dominio crudo.
        // Toda la lógica de encriptación y combinación de fechas se procesa en tu UserService.
        User userTemplate = User.builder()
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .fullName(userRequest.getFullName())
                .isActive(userRequest.getIsActive() != null ? userRequest.getIsActive() : defaultUserActiveStatus)
                .failedLoginAttempts(userRequest.getFailedLoginAttempts())
                .roleName(userRequest.getRoleName())
                .build();

        return userManagementPort.updateUser(id, userTemplate)
                .map(UserResponse::fromUser)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        return userManagementPort.deleteUser(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    private ResponseEntity<UserResponse> createUserWorkflow(UserRequest userRequest, String roleName, boolean isActive) {
        // Mapeo inicial simple. Tu UserService se encargará de encriptar el password y forzar failedLoginAttempts a 0.
        User user = User.builder()
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .fullName(userRequest.getFullName())
                .isActive(isActive)
                .roleName(roleName)
                .build();

        User createdUser = userManagementPort.createUser(user);
        return new ResponseEntity<>(UserResponse.fromUser(createdUser), HttpStatus.CREATED);
    }
}