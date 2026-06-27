package com.posapi.domain.repository.audit;

import com.posapi.domain.model.AuditLog; // Importa el modelo, no la entidad

public interface AuditLogRepository {
    void save(AuditLog auditLog); // Debe recibir AuditLog (modelo)
}