package com.posapi.application.port.role;

import com.posapi.domain.model.role.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleManagementPort {
    Role createRole(Role role);
    Optional<Role> getRoleById(UUID id);
    List<Role> getAllRoles();
    Optional<Role> updateRole(UUID id, Role role);
    boolean deleteRole(UUID id);
}