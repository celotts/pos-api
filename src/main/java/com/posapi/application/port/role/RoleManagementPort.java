package com.posapi.application.port.role;

import com.posapi.domain.model.role.Role;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleManagementPort {
    Role createRole(Role role);
    Optional<Role> getRoleById(UUID id);
    List<RoleResponse> getAllRolesWithUserNames();

    @Transactional(readOnly = true)
    List<Role> getAllRoles();

    Optional<Role> updateRole(UUID id, Role role);
    void deleteRole(UUID id);

    // 🛡️ World-Class: The port now returns a specific, type-safe DTO.
    // This makes the contract clear and robust.
    Optional<RoleResponse> getRoleByIdWithUserNames(UUID id);
}
