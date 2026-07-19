package com.posapi.infrastructure.adapter.output.persistence.repository.audit;

import com.posapi.infrastructure.adapter.output.persistence.entity.audit.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface AuditLogJpaRepository extends JpaRepository<AuditLogEntity, UUID> {
}
