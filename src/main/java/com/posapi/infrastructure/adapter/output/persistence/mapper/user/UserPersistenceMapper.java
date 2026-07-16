package com.posapi.infrastructure.adapter.output.persistence.mapper.user;

import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserPersistenceMapper {

    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }
        return UserEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .fullName(domain.getFullName())
                .isActive(domain.getIsActive())
                // .failedLoginAttempts(domain.getFailedLoginAttempts()) // ELIMINADO: Ya no existe en User
                .role(mapRoleToProxyEntity(domain.getRole())) // Mapear el objeto Role
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .deletedAt(domain.getDeletedAt())
                .createdByUserId(domain.getCreatedByUserId())
                .updatedByUserId(domain.getUpdatedByUserId())
                .deletedByUserId(domain.getDeletedByUserId())
                .createdByRoleId(domain.getCreatedByRoleId())
                .updatedByRoleId(domain.getUpdatedByRoleId())
                .deletedByRoleId(domain.getDeletedByRoleId())
                .build();
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .fullName(entity.getFullName())
                .isActive(entity.getIsActive())
                // .failedLoginAttempts(entity.getFailedLoginAttempts()) // ELIMINADO: Ya no existe en UserEntity
                .role(mapRoleEntityToDomain(entity.getRole())) // Mapear el objeto RoleEntity
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .createdByUserId(entity.getCreatedByUserId())
                .updatedByUserId(entity.getUpdatedByUserId())
                .deletedByUserId(entity.getDeletedByUserId())
                .createdByRoleId(entity.getCreatedByRoleId())
                .updatedByRoleId(entity.getUpdatedByRoleId())
                .deletedByRoleId(entity.getDeletedByRoleId())
                .build();
    }

    private RoleEntity mapRoleToProxyEntity(Role role) {
        if (role == null) {
            return null;
        }
        // Esto crea una referencia a la entidad Role sin cargarla completamente,
        // útil para establecer relaciones sin tener que buscar la entidad completa.
        return RoleEntity.builder().id(role.getId()).name(role.getName()).build(); // Solo necesitamos el ID y el nombre para el proxy
    }

    private Role mapRoleEntityToDomain(RoleEntity roleEntity) {
        if (roleEntity == null) {
            return null;
        }
        return Role.builder()
                .id(roleEntity.getId())
                .name(roleEntity.getName())
                .createdAt(roleEntity.getCreatedAt())
                .updatedAt(roleEntity.getUpdatedAt())
                .deletedAt(roleEntity.getDeletedAt())
                .createdByUserId(roleEntity.getCreatedByUserId())
                .updatedByUserId(roleEntity.getUpdatedByUserId())
                .deletedByUserId(roleEntity.getDeletedByUserId())
                .createdByRoleId(roleEntity.getCreatedByRoleId())
                .updatedByRoleId(roleEntity.getUpdatedByRoleId())
                .deletedByRoleId(roleEntity.getDeletedByRoleId())
                .build();
    }
}
