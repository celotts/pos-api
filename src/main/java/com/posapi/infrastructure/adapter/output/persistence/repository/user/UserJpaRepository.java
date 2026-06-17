package com.posapi.infrastructure.adapter.output.persistence.repository.user;

import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByFullName(String fullName);

    boolean existsByEmail(String email);

    boolean existsByFullName(String fullName);
}