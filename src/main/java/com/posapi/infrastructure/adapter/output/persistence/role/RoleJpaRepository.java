package com.posapi.infrastructure.adapter.output.persistence.role;

import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleJpaRepository extends JpaRepository<RoleEntity, UUID> {

    Optional<RoleEntity> findByName(String name);

    boolean existsByName(String name);
}
