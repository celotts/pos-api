package com.posapi.infrastructure.adapter.output.persistence.repository.role;

import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByName(String name);
    boolean existsByName(String name);
}
