package com.posapi.infrastructure.adapter.output.persistence.entity.user;

import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity; // Importar RoleEntity
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.generator.EventType;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder(toBuilder = true) // Añadido toBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@SQLRestriction("deleted_at IS NULL")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Asumiendo UUIDs auto-generados por la DB o Hibernate
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @Column(name = "email", unique = true, nullable = false)
    @ToString.Include
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    @ToString.Include
    private String fullName;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts; // Este campo no estaba en el dominio User, pero sí en la entidad UserEntity

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role; // Cambiado a RoleEntity

    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "created_by_user_id") // Mapea a created_by_user_id en la DB
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id") // Mapea a updated_by_user_id en la DB
    private UUID updatedByUserId;

    @Column(name = "deleted_by_user_id") // Mapea a deleted_by_user_id en la DB
    private UUID deletedByUserId;

    @Generated(event = EventType.INSERT)
    @Column(name = "created_by_role_id", insertable = false, updatable = false)
    private UUID createdByRoleId;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "updated_by_role_id", insertable = false, updatable = false)
    private UUID updatedByRoleId;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "deleted_by_role_id", insertable = false, updatable = false)
    private UUID deletedByRoleId;

    // ELIMINADO: Los métodos getCreatedByUserId(), getUpdatedByUserId(), etc.
    // son generados automáticamente por Lombok si los campos se llaman createdByUserId, etc.
}
