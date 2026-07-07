package com.posapi.infrastructure.adapter.input.rest.role;

import com.posapi.application.port.role.RoleManagementPort;
import com.posapi.domain.model.role.Role;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleRequest;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleResponse;
import com.posapi.infrastructure.security.SecurityContextHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleManagementPort roleManagementPort;
    private final SecurityContextHelper securityContextHelper;

    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
        Role roleToCreate = Role.builder().name(request.name()).build();
        Role createdRole = roleManagementPort.createRole(roleToCreate);
        return roleManagementPort.getRoleByIdWithUserNames(createdRole.getId())
                .map(responseDto -> new ResponseEntity<>(responseDto, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(roleManagementPort.getAllRolesWithUserNames());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable UUID id) {
        return roleManagementPort.getRoleByIdWithUserNames(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable UUID id, @Valid @RequestBody RoleRequest request) {
        Role roleToUpdate = Role.builder().name(request.name()).build();
        return roleManagementPort.updateRole(id, roleToUpdate)
                .flatMap(updatedRole -> roleManagementPort.getRoleByIdWithUserNames(updatedRole.getId()))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleManagementPort.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
