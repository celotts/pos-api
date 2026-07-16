package com.posapi.application.port.role;

import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleRequest; // Asumo que tendrás un DTO de Request
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleResponse; // Asumo que tendrás un DTO de Response
import com.posapi.domain.model.role.Role; // Entidad de dominio Role
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface RoleManagementPort {

    /**
     * Asigna un rol a un usuario.
     * @param userEmail Email del usuario.
     * @param roleName Nombre del rol a asignar.
     */
    void assignRoleToUser(String userEmail, String roleName);

    /**
     * Crea un nuevo rol.
     * @param request DTO con los datos del rol a crear.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un DTO de respuesta con el rol creado.
     */
    RoleResponse createRole(RoleRequest request, UUID currentUserId);

    @Transactional
    Role createRole(String roleName, UUID currentUserId);

    Role createRole(Role role);

    /**
     * Obtiene un rol por su ID.
     *
     * @param id ID único del rol.
     * @return Un Optional que contiene el DTO de respuesta si el rol existe.
     */
    Optional<Role> getRoleById(UUID id);

    /**
     * Obtiene una lista de todos los roles con nombres de usuario de auditoría.
     * @return Una lista de DTOs de respuesta de roles.
     */
    List<RoleResponse> getAllRolesWithUserNames();

    /**
     * Obtiene un rol por su ID con nombres de usuario de auditoría.
     * @param id ID único del rol.
     * @return Un Optional que contiene el DTO de respuesta si el rol existe.
     */
    Optional<RoleResponse> getRoleByIdWithUserNames(UUID id);

    /**
     * Actualiza un rol existente.
     *
     * @param roleId        ID del rol a actualizar.
     * @param newRoleName   Nuevo nombre del rol.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un DTO de respuesta con el rol actualizado.
     */
    Object updateRole(UUID roleId, String newRoleName, UUID currentUserId);

    /**
     * Elimina lógicamente un rol.
     * @param roleId ID del rol a eliminar.
     * @param currentUserId ID del usuario que realiza la operación.
     */
    void deleteRole(UUID roleId, UUID currentUserId);

    /**
     * Obtiene los detalles de un rol por su ID.
     * @param roleId ID del rol.
     * @return La entidad Role.
     */
    Role getRoleDetails(UUID roleId);

    /**
     * Obtiene todos los roles.
     * @return Una lista de entidades Role.
     */
    List<Role> getAllRoles();

    Optional<Role> updateRole(UUID id, Role role);

    void deleteRole(UUID id);
}
