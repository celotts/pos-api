package com.posapi.domain.model.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true) // Habilitamos toBuilder para mutaciones limpias en controladores/casos de uso
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    private UUID id;
    private String name;

    // 🕒 Marcas de tiempo de Auditoría
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    // 👤 IDs de Usuarios Operativos
    private UUID createdBy;
    private UUID updatedBy;
    private UUID deletedBy;

    // 🛡️ IDs de Roles de Auditoría (Calculados por el Trigger de Postgres)
    private UUID createdByRoleId;
    private UUID updatedByRoleId;
    private UUID deletedByRoleId;
}