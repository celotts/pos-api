package com.posapi.infrastructure.adapter.output.persistence.adapter.audit;

import com.posapi.domain.model.audit.AuditLog; // Import corregido
import com.posapi.domain.repository.audit.AuditLogRepository;
import com.posapi.infrastructure.adapter.output.persistence.mapper.audit.AuditLogPersistenMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.audit.AuditLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogPersistenceAdapter implements AuditLogRepository {

    private final AuditLogJpaRepository jpaRepository;
    private final AuditLogPersistenMapper mapper;

    @Override
    public void save(AuditLog auditLog) {
        var entity = mapper.toEntity(auditLog);
        jpaRepository.save(entity);
    }
}
