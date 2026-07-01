package com.posapi.domain.port.output;

import com.posapi.domain.model.audit.AuditLog;

public interface AuditLogRepository {
    void save(AuditLog auditLog);
}
