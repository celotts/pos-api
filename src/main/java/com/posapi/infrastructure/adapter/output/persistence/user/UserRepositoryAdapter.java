package com.posapi.infrastructure.adapter.output.persistence.user;

import com.posapi.domain.model.user.User;
import com.posapi.domain.repository.user.UserRepository;
import com.posapi.infrastructure.persistence.entity.user.UserEntity;
import com.posapi.infrastructure.persistence.entity.user.UserRole;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Primary
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = toEntity(user);
        UserEntity savedEntity = userJpaRepository.save(userEntity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void delete(User user) {

    }

    // --- Mappers ---
    private UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .fullName(user.getFullName())
                .isActive(user.getIsActive())
                .role(UserRole.valueOf(user.getRole())) // Mapea String a ENUM
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .createdByUserId(user.getCreatedByUserId())
                .updatedByUserId(user.getUpdatedByUserId())
                .deletedByUserId(user.getDeletedByUserId())
                .build();
    }

    private User toDomain(UserEntity userEntity) {
        return User.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .passwordHash(userEntity.getPasswordHash())
                .fullName(userEntity.getFullName())
                .isActive(userEntity.getIsActive())
                .role(userEntity.getRole().name()) // Mapea ENUM a String
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .deletedAt(userEntity.getDeletedAt())
                .createdByUserId(userEntity.getCreatedByUserId())
                .updatedByUserId(userEntity.getUpdatedByUserId())
                .deletedByUserId(userEntity.getDeletedByUserId())
                .build();
    }
}
