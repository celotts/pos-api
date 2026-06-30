package com.posapi.infrastructure.adapter.output.persistence.mapper.role;

import com.posapi.domain.model.role.Role;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RolePersistenceMapper {

    public RoleEntity toEntity(Role domain) {
        return RoleEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .deletedBy(domain.getDeletedBy())
                .build();
    }

    public Role toDomain(RoleEntity entity) {
        return Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .deletedBy(entity.getDeletedBy())
                .build();
    }
}
