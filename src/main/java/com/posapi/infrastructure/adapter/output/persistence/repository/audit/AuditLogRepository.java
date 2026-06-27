package com.posapi.infrastructure.adapter.output.persistence.repository.audit;

import com.posapi.infrastructure.adapter.output.persistence.entity.audit.AuditLogEntity; // <--- IMPORTANTE: Usa la entidad
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

// IMPORTANTE: Debe decir JpaRepository<AuditLogEntity, UUID>
// Si dice AuditLogRepository extends CrudRepository<AuditLog, UUID>, ahí está el error.
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {
}