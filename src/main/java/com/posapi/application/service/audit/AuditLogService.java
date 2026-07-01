package com.posapi.application.service.audit;

import com.posapi.domain.model.audit.AuditLog;
import com.posapi.domain.port.output.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAuditLog(AuditLog log) {
        if (log != null) {
            auditLogRepository.save(log);
        }
    }
}
