package com.posapi.infrastructure.adapter.output.persistence.adapter.audit;

import com.posapi.domain.model.audit.AuditLog;
import com.posapi.domain.port.output.AuditLogRepository;
import com.posapi.infrastructure.adapter.output.persistence.mapper.audit.AuditLogPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.audit.AuditLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogPersistenceAdapter implements AuditLogRepository {

    private final AuditLogJpaRepository jpaRepository;
    private final AuditLogPersistenceMapper mapper;

    @Override
    public void save(AuditLog auditLog) {
        var entity = mapper.toEntity(auditLog);
        jpaRepository.save(entity);
    }
}
