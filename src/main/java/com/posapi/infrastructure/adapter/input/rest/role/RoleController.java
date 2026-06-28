package com.posapi.infrastructure.adapter.input.rest.role;

import com.posapi.application.port.role.RoleManagementPort;
import com.posapi.domain.model.role.Role;
import com.posapi.infrastructure.adapter.input.rest.dto.role.RoleRequest; // Import corregido
import com.posapi.infrastructure.adapter.input.rest.dto.role.RoleResponse; // Import corregido
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleManagementPort roleManagementPort;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
        Role roleToCreate = Role.builder().name(request.name()).build();
        Role createdRole = roleManagementPort.createRole(roleToCreate);
        return new ResponseEntity<>(RoleResponse.fromDomain(createdRole), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = roleManagementPort.getAllRoles().stream()
                .map(RoleResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable UUID id) {
        return roleManagementPort.getRoleById(id)
                .map(RoleResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable UUID id, @Valid @RequestBody RoleRequest request) {
        Role roleToUpdate = Role.builder().name(request.name()).build();
        return roleManagementPort.updateRole(id, roleToUpdate)
                .map(RoleResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        return roleManagementPort.deleteRole(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
