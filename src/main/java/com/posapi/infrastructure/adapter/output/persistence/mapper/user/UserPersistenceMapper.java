package com.posapi.infrastructure.adapter.output.persistence.mapper.user;

import com.posapi.domain.model.user.User;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .isActive(entity.getIsActive())
                .roleName(entity.getRole() != null ? entity.getRole().getName() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // Ajustado para recibir el RoleEntity y usar la propiedad correcta 'passwordHash'
    public UserEntity toEntity(User domain, RoleEntity roleEntity) {
        if (domain == null) return null;
        return UserEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .password(domain.getPassword()) // 🔄 Ajustado a 'passwordHash'
                .fullName(domain.getFullName())
                .isActive(domain.getIsActive())
                .role(roleEntity)                   // 🔄 Relación con el rol resuelta
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}