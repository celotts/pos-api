package com.posapi.infrastructure.adapter.output.persistence.mapper.user;

import com.posapi.domain.model.user.User;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserPersistenceMapper {

    // IDs predecibles definidos exactamente en tu script SQL base
    private static final UUID ADMIN_ROLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID USER_ROLE_ID  = UUID.fromString("00000000-0000-0000-0000-000000000002");

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .fullName(entity.getFullName())
                .isActive(entity.getIsActive())
                .failedLoginAttempts(entity.getFailedLoginAttempts())
                .roleName(entity.getRole() != null ? entity.getRole().getName() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) return null;

        // Resolvemos el ID del rol de forma predictiva conforme a los inserts fijos de la DB
        UUID targetRoleId = USER_ROLE_ID; // default
        String roleName = domain.getRoleName();

        if (roleName != null && roleName.equalsIgnoreCase("ADMIN")) {
            targetRoleId = ADMIN_ROLE_ID;
        }

        RoleEntity roleEntity = RoleEntity.builder()
                .id(targetRoleId) // 🟢 Asigna el ID exacto que PostgreSQL espera para la relación
                .name(roleName != null ? roleName.toUpperCase() : "USER")
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