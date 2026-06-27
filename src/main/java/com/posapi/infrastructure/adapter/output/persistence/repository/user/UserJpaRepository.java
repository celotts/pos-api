package com.posapi.infrastructure.adapter.output.persistence.repository.user;

import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    
    Optional<UserEntity> findByEmail(String email);

    // Devuelve una lista para manejar de forma segura nombres duplicados.
    List<UserEntity> findAllByFullName(String fullName);

    boolean existsByEmail(String email);

    boolean existsByFullName(String fullName);


    @Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE u.role.name = :roleName")
    boolean existsByRoleName(@Param("roleName") String roleName);
}