package com.posapi.infrastructure.adapter.output.persistence.mapper.user;

import com.posapi.domain.model.user.User;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        // Creates a "proxy" entity for the role with just the ID.
        RoleEntity roleEntity = null;
        if (domain.getRoleId() != null) {
            roleEntity = new RoleEntity();
            roleEntity.setId(domain.getRoleId());
        }

        return UserEntity.builder()
                // 🛡️ THIS IS THE CRITICAL LINE THAT FIXES THE ERROR
                .id(domain.getId())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .fullName(domain.getFullName())
                .isActive(domain.getIsActive())
                .failedLoginAttempts(domain.getFailedLoginAttempts())
                .role(roleEntity)
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
                .failedLoginAttempts(entity.getFailedLoginAttempts())
                .roleId(entity.getRoleId())
                .roleName(entity.getRole() != null ? entity.getRole().getName() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}