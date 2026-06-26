package com.posapi.infrastructure.adapter.output.persistence.mapper.user;

import com.posapi.domain.exception.InvariantException;
import com.posapi.domain.model.user.User;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserPersistenceMapper {

    public UserEntity toEntity(User domain) {
        if (domain == null) {
            throw new InvariantException("User domain object cannot be null when mapping to an entity.");
        }
        if (domain.getId() == null) {
            throw new InvariantException("Domain object ID cannot be null when mapping to an entity.");
        }

        return UserEntity.builder()
                // 🛡️ THIS IS THE CRITICAL LINE THAT FIXES THE ERROR
                .id(domain.getId())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .fullName(domain.getFullName())
                .isActive(domain.getIsActive())
                .failedLoginAttempts(domain.getFailedLoginAttempts())
                .role(mapRoleIdToProxyEntity(domain.getRoleId()))
                .build();
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            throw new InvariantException("User entity object cannot be null when mapping to a domain object.");
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

    private RoleEntity mapRoleIdToProxyEntity(UUID roleId) {
        if (roleId == null) {
            throw new InvariantException("RoleId cannot be null when mapping to a RoleEntity proxy.");
        }
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(roleId);
        return roleEntity;
    }
}