package com.posapi.infrastructure.adapter.input.rest.role.mapper;

import com.posapi.domain.model.role.Role;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleRequest;
import com.posapi.infrastructure.adapter.input.rest.role.dto.RoleResponse;
import org.springframework.stereotype.Component;

@Component
public class RoleRestMapper {

    // Convierte el DTO de entrada al objeto de Dominio
    public Role toDomain(RoleRequest request) {
        if (request == null) {
            return null;
        }

        return Role.builder()
                .name(request.name())
                .build();
    }

    // Convierte el Dominio al DTO de salida (agregando los nombres de auditoría)
    // CORREGIDO: Se añade String deletedByName a la firma del método para recibirlo desde el Service
    public RoleResponse toResponse(Role role, String createdByName, String updatedByName, String deletedByName) {
        if (role == null) {
            return null;
        }

        return RoleResponse.fromDomain(role, createdByName, updatedByName, deletedByName);
    }
}
