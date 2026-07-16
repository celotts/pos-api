package com.posapi.domain.model.category;

import jakarta.persistence.*; // Importar las anotaciones de JPA
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; // Añadir NoArgsConstructor
import lombok.AllArgsConstructor; // Añadir AllArgsConstructor

import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true) // Añadir toBuilder para facilitar actualizaciones
@NoArgsConstructor
@AllArgsConstructor
@Entity // Indicar que es una entidad JPA
@Table(name = "categories") // Mapear a la tabla 'categories'
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Generación automática de UUID
    private UUID id;

    @Column(nullable = false, unique = true) // 'name' es NOT NULL y UNIQUE
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

    @Column(name = "deleted_by_user_id") // Añadido para consistencia con auditoría
    private UUID deletedByUserId;

    @Column(name = "created_by_role_id") // Añadido para auditoría de rol
    private UUID createdByRoleId;

    @Column(name = "updated_by_role_id") // Añadido para auditoría de rol
    private UUID updatedByRoleId;

    @Column(name = "deleted_by_role_id") // Añadido para auditoría de rol
    private UUID deletedByRoleId;
}
