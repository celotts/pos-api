package com.posapi.application.port.role;

import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleRequest;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleResponse;
import com.posapi.shared.dto.PageResponse; // AÑADIDO
import org.springframework.data.domain.Pageable; // AÑADIDO

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleManagementPort {

    RoleResponse createRole(RoleRequest request); // CORREGIDO: Solo un método createRole, sin currentUserId aquí

    Optional<RoleResponse> getRoleById(UUID id); // CORREGIDO: Devuelve Optional<RoleResponse>

    List<RoleResponse> getAllRoles(); // CORREGIDO: Devuelve List<RoleResponse>

    PageResponse<RoleResponse> getAllRoles(Pageable pageable); // AÑADIDO: Método paginado

    Optional<RoleResponse> updateRole(UUID id, RoleRequest request); // CORREGIDO: Usa RoleRequest y devuelve Optional<RoleResponse>

    void deleteRole(UUID id); // CORREGIDO: Retorna void
}
