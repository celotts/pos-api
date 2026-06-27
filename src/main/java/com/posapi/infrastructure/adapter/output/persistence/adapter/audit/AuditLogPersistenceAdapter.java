package com.posapi.infrastructure.adapter.output.persistence.adapter.audit;

import com.posapi.domain.model.AuditLog; // Modelo de dominio
import com.posapi.domain.repository.audit.AuditLogRepository; // Interfaz de dominio
import com.posapi.infrastructure.adapter.output.persistence.mapper.audit.AuditLogMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.audit.AuditLogJpaRepository; // Repository JPA
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogPersistenceAdapter implements AuditLogRepository {

    // 1. INYECTA EL REPOSITORIO JPA, NO EL DOMAIN REPOSITORY
    private final AuditLogJpaRepository jpaRepository;
    private final AuditLogMapper mapper;

    // 2. RECIBE EL MODELO DE DOMINIO, NO LA ENTIDAD
    @Override
    public void save(AuditLog auditLog) {
        // 3. MAPEAS A ENTIDAD DENTRO DEL MÉTODO
        var entity = mapper.toEntity(auditLog);

        // 4. GUARDAS USANDO EL REPOSITORIO JPA
        jpaRepository.save(entity);
    }
}