package com.posapi.infrastructure.adapter.input.rest.user;

import com.posapi.application.port.user.UserManagementPort;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.RoleRepository;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserRequest;
import com.posapi.infrastructure.adapter.input.rest.user.dto.UserResponse;
import com.posapi.infrastructure.adapter.input.rest.user.mapper.UserRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor; // Usar RequiredArgsConstructor para simplificar
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor // Inyecta dependencias automáticamente
public class UserController {

    private final UserManagementPort userManagementPort;
    private final RoleRepository roleRepository; // Kept for creation/update logic
    private final UserRestMapper userRestMapper; // 👈 Inject the new Mapper

    @Value("${app.roles.USER:USER}")
    private String defaultUserRole;

    @Value("${app.user.default.active:true}")
    private boolean defaultUserActiveStatus;

    @Value("${app.roles.ADMIN:ADMIN}")
    private String adminRole;

    @Value("${app.user.admin-creation.active:true}")
    private boolean adminActiveStatus;

    @PostMapping("/register")
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
                .map(userRestMapper::toResponse) // Use the mapper
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return userManagementPort.getUserByEmail(email)
                .map(userRestMapper::toResponse) // Use the mapper
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userManagementPort.getAllUsers().stream()
                .map(userRestMapper::toResponse) // Use the mapper
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UserRequest userRequest) {
        // Obtenemos el ID del rol si viene en el request
        // Es mejor fallar rápido si el rol no existe.
        UUID roleId = null;
        if (userRequest.getRoleName() != null && !userRequest.getRoleName().isBlank()) {
            roleId = roleRepository.findByName(userRequest.getRoleName())
                    .map(Role::getId)
                    .orElseThrow(() -> new IllegalArgumentException("El rol especificado no existe: " + userRequest.getRoleName()));
        }

        User userTemplate = User.builder()
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .fullName(userRequest.getFullName())
                .isActive(userRequest.getIsActive() != null ? userRequest.getIsActive() : true)
                .roleId(roleId) // Pasamos el ID del rol validado
                .build();

        return userManagementPort.updateUser(id, userTemplate)
                .map(userRestMapper::toResponse) // Use the mapper
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
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Rol no encontrado: " + roleName));

        User user = User.builder()
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .fullName(userRequest.getFullName())
                .isActive(isActive)
                .roleId(role.getId())
                .failedLoginAttempts(0)
                .build();

        User createdUser = userManagementPort.createUser(user);
        return new ResponseEntity<>(userRestMapper.toResponse(createdUser), HttpStatus.CREATED);
    }
}