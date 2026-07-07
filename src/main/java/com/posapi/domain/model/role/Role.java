package com.posapi.domain.model.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    private UUID id;
    private String name;

    // 🕒 Unificado a Instant. LocalDateTime ya no es necesario.
    @Builder.Default
    private Instant createdAt = Instant.now();

    private Instant updatedAt;
    private Instant deletedAt;

    // 👤 IDs de Usuarios Operativos
    private UUID createdBy;
    private UUID updatedBy;
    private UUID deletedBy;

    // 🛡️ IDs de Roles de Auditoría
    private UUID createdByRoleId;
    private UUID updatedByRoleId;
    private UUID deletedByRoleId;

    public void markAsUpdated(UUID updatedBy) {
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
    }
}
