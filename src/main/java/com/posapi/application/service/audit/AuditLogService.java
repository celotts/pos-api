package com.posapi.application.service.audit;

import com.posapi.domain.model.AuditLog;
import com.posapi.domain.repository.audit.AuditLogRepository; // Interfaz de dominio
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    // Ahora inyectamos la interfaz del dominio
    private final AuditLogRepository auditLogRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAuditLog(AuditLog log) {
        if (log != null) {
            // El servicio ya no conoce AuditLogEntity ni AuditLogMapper
            auditLogRepository.save(log);
        }
    }
}