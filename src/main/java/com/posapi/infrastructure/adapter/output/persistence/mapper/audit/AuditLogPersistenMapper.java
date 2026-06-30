package com.posapi.infrastructure.adapter.output.persistence.mapper.audit;

import com.posapi.domain.model.audit.AuditLog;
import com.posapi.infrastructure.adapter.output.persistence.entity.audit.AuditLogEntity;
import org.springframework.stereotype.Component;

@Component
public class AuditLogPersistenMapper {

    public AuditLogEntity toEntity(AuditLog domain) {
        if (domain == null) return null;

        return AuditLogEntity.builder()
                .id(domain.getId())
                .tableName(domain.getTableName())
                .recordId(domain.getRecordId())
                .action(domain.getAction()) // Ahora los tipos coinciden (enum)
                .oldValue(domain.getOldValue())
                .newValue(domain.getNewValue())
                .ipAddress(domain.getIpAddress())
                .userAgent(domain.getUserAgent())
                .userId(domain.getUserId())
                .build();
    }

    public AuditLog toDomain(AuditLogEntity entity) {
        if (entity == null) return null;

        return AuditLog.builder()
                .id(entity.getId())
                .tableName(entity.getTableName())
                .recordId(entity.getRecordId())
                .action(entity.getAction())
                .oldValue(entity.getOldValue())
                .newValue(entity.getNewValue())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .createdAt(entity.getCreatedAt())
                .userId(entity.getUserId())
                .build();
    }
}
