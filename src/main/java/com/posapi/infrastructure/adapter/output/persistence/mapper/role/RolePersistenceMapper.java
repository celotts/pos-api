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
                // Asegura un ID si el dominio no lo inicializó todavía
                .id(domain.getId() != null ? domain.getId() : UUID.randomUUID())
                .name(domain.getName())
                // 🕒 Omitimos deliberadamente createdAt y updatedAt para que actúe Postgres
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .deletedAt(domain.getDeletedAt())
                .deletedBy(domain.getDeletedBy())
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
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .deletedAt(entity.getDeletedAt())
                .deletedBy(entity.getDeletedBy())
                // 🛡️ Sincronizados de forma limpia con tu POJO de dominio purificado
                .createdByRoleId(entity.getCreatedByRoleId())
                .updatedByRoleId(entity.getUpdatedByRoleId())
                .deletedByRoleId(entity.getDeletedByRoleId())
                .build();
    }
}
