package com.posapi.infrastructure.adapter.output.persistence.repository.shift;

import com.posapi.infrastructure.adapter.output.persistence.entity.shift.ShiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShiftJpaRepository extends JpaRepository<ShiftEntity, UUID> {
}
