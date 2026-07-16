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
    @GeneratedValue(strategy = GenerationType.AUTO) // Asumiendo UUIDs auto-generados por la DB o Hibernate
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


}
