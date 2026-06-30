package com.posapi.domain.model.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    private UUID id;
    private String tableName;
    private UUID recordId;
    private AuditAction action; // Usando el enum
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private String userAgent;
    private Instant createdAt;
    private UUID userId;
}
