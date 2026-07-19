package com.posapi.application.port.role;

import com.posapi.domain.model.role.Role;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleInputPort {
    // Operaciones para crear, actualizar y eliminar roles
    Role createRole(String roleName, UUID currentUserId);
    Role updateRole(UUID roleId, String newRoleName, UUID currentUserId);
    void deleteRole(UUID roleId, UUID currentUserId);

    // Operaciones para recuperar roles
    Optional<Role> getRoleById(UUID id);
    List<Role> getAllRoles();

    // Operaciones que devuelven DTOs para la capa de presentación
    Optional<RoleResponse> getRoleByIdWithUserNames(UUID id);
    List<RoleResponse> getAllRolesWithUserNames();

    // Lógica de negocio específica
    void assignRoleToUser(String userEmail, String roleName);
}
