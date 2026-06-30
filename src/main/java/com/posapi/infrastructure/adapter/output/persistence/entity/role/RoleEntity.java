package com.posapi.infrastructure.adapter.output.persistence.entity.role;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@SQLRestriction("deleted_at IS NULL")
public class RoleEntity {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @Column(name = "name", unique = true, nullable = false, length = 50)
    @ToString.Include
    private String name;

    // 🕒 El Trigger de Postgres maneja el tiempo. Java solo lo lee.
    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    // 👤 IDs de los Usuarios Operativos (Enviados desde Java mediante el JWT)
    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    // 🛡️ IDs de los Roles de Auditoría (Calculados 100% de forma automática por el Trigger)
    @Column(name = "created_by_role_id", insertable = false, updatable = false)
    private UUID createdByRoleId;

    @Column(name = "updated_by_role_id", insertable = false, updatable = false)
    private UUID updatedByRoleId;

    @Column(name = "deleted_by_role_id", insertable = false, updatable = false)
    private UUID deletedByRoleId;
}