package com.posapi.domain.model.role;

import jakarta.persistence.*; // Importar las anotaciones de JPA
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true) // Añadido toBuilder para facilitar actualizaciones
@NoArgsConstructor
@AllArgsConstructor
@Entity // Indicar que es una entidad JPA
@Table(name = "roles") // Mapear a la tabla 'roles'
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "created_by_user_id") // Corregido para coincidir con SQL
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id") // Corregido para coincidir con SQL
    private UUID updatedByUserId;

    @Column(name = "deleted_by_user_id") // Corregido para coincidir con SQL
    private UUID deletedByUserId;

    @Column(name = "created_by_role_id") // Añadido para auditoría de rol
    private UUID createdByRoleId;

    @Column(name = "updated_by_role_id") // Añadido para auditoría de rol
    private UUID updatedByRoleId;

    @Column(name = "deleted_by_role_id") // Añadido para auditoría de rol
    private UUID deletedByRoleId;

    // Método estático para crear un nuevo rol
    public static Role createNew(String name, UUID currentUserId, UUID currentUserRoleId) {
        return Role.builder()
                .id(UUID.randomUUID())
                .name(name)
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByRoleId(currentUserRoleId)
                .build();
    }

    // Método de dominio para actualizar el nombre del rol
    public void updateName(String newName, UUID updatedByUserId, UUID updatedByRoleId) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Role name cannot be null or empty.");
        }
        this.name = newName;
        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByRoleId = updatedByRoleId;
    }

    // Método de dominio para marcar el rol como eliminado (borrado lógico)
    public void markAsDeleted(UUID deletedByUserId, UUID deletedByRoleId) {
        if (this.deletedAt == null) { // Solo si no ha sido eliminado lógicamente antes
            this.deletedAt = Instant.now();
            this.deletedByUserId = deletedByUserId;
            this.deletedByRoleId = deletedByRoleId;
        }
    }
}
