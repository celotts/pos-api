package com.posapi.domain.model;

import lombok.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor // Necesario para 'new AuditLog()'
@AllArgsConstructor
public class AuditLog {
    private UUID id;
    private String tableName;
    private UUID recordId;
    private String action;
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private String userAgent;
    private Instant createdAt;
    private UUID userId;
    private UUID roleId;

    // Agrega estos campos para que tu AuditAspect funcione:
    private String username;
    private String method;
    private String status;
    private String errorMessage;
    private String payload;
    private LocalDateTime timestamp;
}