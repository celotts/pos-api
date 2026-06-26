package com.posapi.infrastructure.adapter.output.persistence.mapper.user;

import com.posapi.domain.model.user.User;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .fullName(entity.getFullName())
                .isActive(entity.getIsActive())
                .failedLoginAttempts(entity.getFailedLoginAttempts())
                .roleId(entity.getRole() != null ? entity.getRole().getId() : null) // Solo asignamos el ID
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) return null;

        // Construimos el RoleEntity usando el ID que viene del dominio
        RoleEntity roleEntity = RoleEntity.builder()
                .id(domain.getRoleId())
                .build();

        return UserEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .fullName(domain.getFullName())
                .isActive(domain.getIsActive())
                .failedLoginAttempts(domain.getFailedLoginAttempts() != null ? domain.getFailedLoginAttempts() : 0)
                .role(roleEntity)
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}