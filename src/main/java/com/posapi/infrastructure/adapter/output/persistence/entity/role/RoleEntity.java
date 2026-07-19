package com.posapi.infrastructure.adapter.output.persistence.entity.role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder(toBuilder = true) // Añadido toBuilder para facilitar actualizaciones
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

    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "created_by_user_id") // Mapea a created_by_user_id en la DB
    private UUID createdByUserId; // Nombre del campo corregido

    @Column(name = "updated_by_user_id") // Mapea a updated_by_user_id en la DB
    private UUID updatedByUserId; // Nombre del campo corregido

    @Column(name = "deleted_by_user_id") // Mapea a deleted_by_user_id en la DB
    private UUID deletedByUserId; // Nombre del campo corregido

    @Generated(event = EventType.INSERT)
    @Column(name = "created_by_role_id", insertable = false, updatable = false)
    private UUID createdByRoleId;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "updated_by_role_id", insertable = false, updatable = false)
    private UUID updatedByRoleId;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "deleted_by_role_id", insertable = false, updatable = false)
    private UUID deletedByRoleId;

    // ELIMINADO: Los métodos getCreatedByUserId(), getUpdatedByUserId(), getDeletedByUserId()
    // son generados automáticamente por Lombok si los campos se llaman createdByUserId, etc.
    // Si los campos se llamaran createdBy, updatedBy, deletedBy, entonces los getters serían getCreatedBy(), etc.
    // Pero como los campos ya tienen el nombre completo, Lombok los genera correctamente.
}
