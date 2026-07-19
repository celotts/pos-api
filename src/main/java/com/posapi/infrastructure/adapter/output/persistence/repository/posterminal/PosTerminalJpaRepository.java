package com.posapi.infrastructure.adapter.output.persistence.repository.posterminal;

import com.posapi.infrastructure.adapter.output.persistence.entity.posterminal.PosTerminalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PosTerminalJpaRepository extends JpaRepository<PosTerminalEntity, UUID> {
    Optional<PosTerminalEntity> findByName(String name);
    boolean existsByName(String name);
}
