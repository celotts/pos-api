package com.posapi.infrastructure.adapter.output.persistence.mapper.role;

import com.posapi.domain.model.role.Role;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
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
                // Si ambos son Instant, simplemente usa: .createdAt(domain.getCreatedAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
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
                // Si ambos son Instant, simplemente usa: .createdAt(entity.getCreatedAt())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .deletedAt(entity.getDeletedAt())
                .deletedBy(entity.getDeletedBy())
                .createdByRoleId(entity.getCreatedByRoleId())
                .updatedByRoleId(entity.getUpdatedByRoleId())
                .deletedByRoleId(entity.getDeletedByRoleId())
                .build();
    }
}