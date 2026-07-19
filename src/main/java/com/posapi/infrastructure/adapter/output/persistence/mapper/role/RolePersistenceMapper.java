package com.posapi.infrastructure.adapter.output.persistence.mapper.role;

import com.posapi.domain.model.role.Role;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RolePersistenceMapper {

    public RoleEntity toEntity(Role domain) {
        if (domain == null) {
            return null;
        }

        return RoleEntity.builder()
                .id(domain.getId() != null ? domain.getId() : UUID.randomUUID())
                .name(domain.getName())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .deletedAt(domain.getDeletedAt())
                // CORREGIDO: Usar los campos de auditoría correctos de la entidad de dominio
                .createdByUserId(domain.getCreatedByUserId())
                .updatedByUserId(domain.getUpdatedByUserId())
                .deletedByUserId(domain.getDeletedByUserId())
                .createdByRoleId(domain.getCreatedByRoleId())
                .updatedByRoleId(domain.getUpdatedByRoleId())
                .deletedByRoleId(domain.getDeletedByRoleId())
                .build();
    }

    public Role toDomain(RoleEntity entity) {
        if (entity == null) {
            return null;
        }

        return Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                // CORREGIDO: Usar los campos de auditoría correctos de la entidad de persistencia
                .createdByUserId(entity.getCreatedByUserId())
                .updatedByUserId(entity.getUpdatedByUserId())
                .deletedByUserId(entity.getDeletedByUserId())
                .createdByRoleId(entity.getCreatedByRoleId())
                .updatedByRoleId(entity.getUpdatedByRoleId())
                .deletedByRoleId(entity.getDeletedByRoleId())
                .build();
    }
}
