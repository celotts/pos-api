package com.posapi.infrastructure.adapter.output.persistence.entity.category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true) // Añadir toBuilder para facilitar actualizaciones
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class CategoryEntity {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at") // Añadido
    private Instant deletedAt;

    @Column(name = "created_by_user_id", updatable = false) // Corregido el nombre de la columna
    private UUID createdByUserId; // Corregido el nombre del campo

    @Column(name = "updated_by_user_id") // Corregido el nombre de la columna
    private UUID updatedByUserId; // Corregido el nombre del campo

    @Column(name = "deleted_by_user_id") // Añadido
    private UUID deletedByUserId;

    @Column(name = "created_by_role_id", updatable = false) // Añadido
    private UUID createdByRoleId;

    @Column(name = "updated_by_role_id") // Añadido
    private UUID updatedByRoleId;

    @Column(name = "deleted_by_role_id") // Añadido
    private UUID deletedByRoleId;
}
