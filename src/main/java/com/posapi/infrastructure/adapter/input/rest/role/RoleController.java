package com.posapi.infrastructure.adapter.input.rest.role;

import com.posapi.application.port.role.RoleManagementPort;
import com.posapi.domain.model.role.Role;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleRequest;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleManagementPort roleManagementPort;

    public RoleController(RoleManagementPort roleManagementPort) {
        this.roleManagementPort = roleManagementPort;
    }

    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest roleRequest) {
        Role roleTemplate = Role.builder()
                .name(roleRequest.getName())
                .build();
        Role createdRole = roleManagementPort.createRole(roleTemplate);
        return ResponseEntity.status(HttpStatus.CREATED).body(RoleResponse.fromDomain(createdRole));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable UUID id) {
        Role role = roleManagementPort.getRoleById(id);
        return ResponseEntity.ok(RoleResponse.fromDomain(role));
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = roleManagementPort.getAllRoles().stream()
                .map(RoleResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody RoleRequest roleRequest) {
        Role role = Role.builder()
                .id(id)
                .name(roleRequest.getName())
                .build();
        Role updatedRole = roleManagementPort.updateRole(id, role);
        return ResponseEntity.ok(RoleResponse.fromDomain(updatedRole));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable @NotNull UUID id) {
        roleManagementPort.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
