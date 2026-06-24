package com.posapi.application.port.role;

import com.posapi.domain.model.role.Role;
import java.util.List;
import java.util.UUID;

public interface RoleManagementPort {
    Role createRole(Role role);
    Role getRoleById(UUID id);
    List<Role> getAllRoles();
    Role updateRole(UUID id, Role role);
    void deleteRole(UUID id);
}
