package com.posapi.infrastructure.adapter.output.persistence.mapper.user;

import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import com.posapi.infrastructure.adapter.output.persistence.repository.role.RoleJpaRepository; // Importar RoleJpaRepository
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserPersistenceMapper {

    private final RoleJpaRepository roleJpaRepository; // Inyectar RoleJpaRepository

    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }
        return UserEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .fullName(domain.getFullName())
                .address(domain.getAddress())
                .phone(domain.getPhone())
                .phone2(domain.getPhone2())
                .isActive(domain.getIsActive())
                .role(mapRoleToManagedEntity(domain.getRole())) // Usar el nuevo método
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
                .address(entity.getAddress())
                .phone(entity.getPhone())
                .phone2(entity.getPhone2())
                .isActive(entity.getIsActive())
                .role(mapRoleEntityToDomain(entity.getRole()))
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

    public UserEntity updateEntityFromDomain(User domain, UserEntity entity) {
        if (domain == null || entity == null) {
            return entity;
        }

        if (domain.getEmail() != null) {
            entity.setEmail(domain.getEmail());
        }
        if (domain.getPassword() != null) {
            entity.setPassword(domain.getPassword());
        }
        if (domain.getFullName() != null) {
            entity.setFullName(domain.getFullName());
        }
        if (domain.getAddress() != null) {
            entity.setAddress(domain.getAddress());
        }
        if (domain.getPhone() != null) {
            entity.setPhone(domain.getPhone());
        }
        if (domain.getPhone2() != null) {
            entity.setPhone2(domain.getPhone2());
        }
        if (domain.getIsActive() != null) {
            entity.setIsActive(domain.getIsActive());
        }
        if (domain.getRole() != null) {
            entity.setRole(mapRoleToManagedEntity(domain.getRole())); // Usar el nuevo método
        }

        if (domain.getUpdatedAt() != null) {
            entity.setUpdatedAt(domain.getUpdatedAt());
        } else {
            entity.setUpdatedAt(Instant.now());
        }
        if (domain.getUpdatedByUserId() != null) {
            entity.setUpdatedByUserId(domain.getUpdatedByUserId());
        }
        if (domain.getUpdatedByRoleId() != null) {
            entity.setUpdatedByRoleId(domain.getUpdatedByRoleId());
        }
        if (domain.getDeletedAt() != null) {
            entity.setDeletedAt(domain.getDeletedAt());
        }
        if (domain.getDeletedByUserId() != null) {
            entity.setDeletedByUserId(domain.getDeletedByUserId());
        }
        if (domain.getDeletedByRoleId() != null) {
            entity.setDeletedByRoleId(domain.getDeletedByRoleId());
        }

        return entity;
    }

    // Nuevo método para mapear un Role de dominio a una RoleEntity gestionada
    private RoleEntity mapRoleToManagedEntity(Role role) {
        if (role == null) {
            return null;
        }
        // Buscar la RoleEntity en el repositorio para asegurar que esté gestionada
        return roleJpaRepository.findById(role.getId())
                .orElseGet(() -> RoleEntity.builder() // Si no se encuentra (caso de creación), construir una nueva
                        .id(role.getId())
                        .name(role.getName())
                        .createdAt(role.getCreatedAt())
                        .updatedAt(role.getUpdatedAt())
                        .deletedAt(role.getDeletedAt())
                        .createdByUserId(role.getCreatedByUserId())
                        .updatedByUserId(role.getUpdatedByUserId())
                        .deletedByUserId(role.getDeletedByUserId())
                        .createdByRoleId(role.getCreatedByRoleId())
                        .updatedByRoleId(role.getUpdatedByRoleId())
                        .deletedByRoleId(role.getDeletedByRoleId())
                        .build());
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
