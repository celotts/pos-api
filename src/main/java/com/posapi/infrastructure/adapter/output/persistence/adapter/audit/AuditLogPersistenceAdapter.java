package com.posapi.infrastructure.adapter.output.persistence.adapter.audit;

import com.posapi.domain.model.audit.AuditLog;
import com.posapi.domain.port.output.AuditLogRepository; // Este es el puerto (Dominio)
import com.posapi.infrastructure.adapter.output.persistence.repository.audit.AuditLogJpaRepository; // Este es el de JPA
import com.posapi.infrastructure.adapter.output.persistence.mapper.audit.AuditLogPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@RequiredArgsConstructor
public class AuditLogPersistenceAdapter implements AuditLogRepository {

    // Cambia el tipo: debe ser el repositorio de JPA, no el puerto de dominio
    private final AuditLogJpaRepository jpaRepository;
    private final AuditLogPersistenceMapper mapper;

    @Override
    public void save(AuditLog auditLog) {
        var entity = mapper.toEntity(auditLog);
        jpaRepository.save(entity); // Ahora esto funcionará porque jpaRepository sabe guardar AuditLogEntity
    }
}
