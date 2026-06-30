package com.posapi.domain.repository.audit;

import com.posapi.domain.model.audit.AuditLog; // Import corregido

public interface AuditLogRepository {
    void save(AuditLog auditLog);
}
