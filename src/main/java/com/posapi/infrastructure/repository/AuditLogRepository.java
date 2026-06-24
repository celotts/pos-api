package com.posapi.infrastructure.repository;

import com.posapi.domain.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> { // Ajusta tu entidad e ID tipo
}