package com.posapi.infrastructure.adapter.output.persistence.mapper.user;

import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
                .role(mapRoleToProxyEntity(domain.getRole()))
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .deletedAt(domain.getDeletedAt())
                .createdByUserId(domain.getCreatedByUserId())
                .updatedByUserId(domain.getUpdatedByUserId())
                .deletedByUserId(domain.getDeletedByUserId())
                // CORREGIDO: Eliminados los campos *byRoleId ya que no existen en la tabla 'users' del DDL
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
                .role(mapRoleEntityToDomain(entity.getRole()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .createdByUserId(entity.getCreatedByUserId())
                .updatedByUserId(entity.getUpdatedByUserId())
                .deletedByUserId(entity.getDeletedByUserId())
                // CORREGIDO: Si el dominio User requiere estos campos, se infieren del rol de auditoría correspondiente del usuario que ejecutó la acción si estuviese cargado, o se dejan nulos ya que la DB se encarga mediante triggers
                .createdByRoleId(null)
                .updatedByRoleId(null)
                .deletedByRoleId(null)
                .build();
    }

    private RoleEntity mapRoleToProxyEntity(Role role) {
        if (role == null) {
            return null;
        }
        return RoleEntity.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
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
