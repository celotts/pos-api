package com.posapi.infrastructure.adapter.input.rest.user;

import com.posapi.application.port.user.UserManagementPort;
import com.posapi.domain.model.user.User;
import com.posapi.infrastructure.adapter.input.rest.dto.user.UserRequest;
import com.posapi.infrastructure.adapter.input.rest.dto.user.UserResponse;
import com.posapi.infrastructure.adapter.input.rest.user.mapper.UserRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserManagementPort userManagementPort;
    private final UserRestMapper userRestMapper;

    // 🛡️ Endpoint público, no necesita autorización
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest userRequest) {
        User userTemplate = userRestMapper.toDomain(userRequest);
        User createdUser = userManagementPort.createUser(userTemplate);
        return new ResponseEntity<>(userRestMapper.toResponse(createdUser), HttpStatus.CREATED);
    }

    // 🛡️ Requiere que el usuario esté autenticado
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return userManagementPort.getUserById(id)
                .map(userRestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🛡️ Requiere que el usuario esté autenticado
    @GetMapping("/email/{email}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return userManagementPort.getUserByEmail(email)
                .map(userRestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🛡️ Requiere rol de ADMIN para ver todos los usuarios
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> users = userManagementPort.getAllUsers(pageable)
                .map(userRestMapper::toResponse);
        return ResponseEntity.ok(users);
    }

    // 🛡️ Requiere rol de ADMIN para modificar usuarios
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UserRequest userRequest) {
        User userTemplate = userRestMapper.toDomain(userRequest);
        return userManagementPort.updateUser(id, userTemplate)
                .map(userRestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🛡️ Requiere rol de ADMIN para cambiar el estado de un usuario
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> toggleUserStatus(@PathVariable UUID id, @RequestParam boolean active) {
        User userUpdate = User.builder().isActive(active).build();
        return userManagementPort.updateUser(id, userUpdate)
                .map(userRestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🛡️ Requiere rol de ADMIN para eliminar usuarios
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        return userManagementPort.deleteUser(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
