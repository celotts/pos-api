package com.posapi.infrastructure.adapter.output.persistence.mapper.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.posapi.domain.model.AuditLog;
import com.posapi.infrastructure.adapter.output.persistence.entity.audit.AuditLogEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor // Esto inyecta el ObjectMapper automáticamente
public class AuditLogMapper {

    private final ObjectMapper objectMapper;

    public AuditLogEntity toEntity(AuditLog domain) {
        try {
            return AuditLogEntity.builder()
                    .id(domain.getId())
                    .tableName(domain.getTableName())
                    .recordId(domain.getRecordId())
                    .action(domain.getAction())
                    // Aquí hacemos la magia de convertir el objeto a String JSON
                    .oldValue(domain.getOldValue() != null ? objectMapper.writeValueAsString(domain.getOldValue()) : null)
                    .newValue(domain.getNewValue() != null ? objectMapper.writeValueAsString(domain.getNewValue()) : null)
                    .ipAddress(domain.getIpAddress())
                    .userAgent(domain.getUserAgent())
                    .createdAt(domain.getCreatedAt())
                    .userId(domain.getUserId())
                    .roleId(domain.getRoleId())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error convirtiendo objeto a JSON para auditoría", e);
        }
    }
}