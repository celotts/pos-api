package com.posapi.infrastructure.adapter.output.persistence.entity.supplier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data; // Cambiado de @Getter a @Data para incluir @Setter
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated; // Importar Generated
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.generator.EventType;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "suppliers")
@Data // Cambiado de @Getter a @Data para incluir @Setter
@Builder(toBuilder = true) // Añadido toBuilder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class SupplierEntity {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false, length = 13)
    private String rfc;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "tax_regimen", nullable = false)
    private String taxRegimen;

    @Column(name = "contact_email")
    private String contactEmail;

    @Generated(event = EventType.INSERT) // Añadido @Generated
    @Column(name = "created_at", updatable = false, insertable = false) // Corregido insertable
    private Instant createdAt;

    @Generated(event = {EventType.INSERT, EventType.UPDATE}) // Añadido @Generated
    @Column(name = "updated_at", insertable = false) // Corregido insertable
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "created_by_user_id", updatable = false) // Renombrado y añadido updatable
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id") // Renombrado
    private UUID updatedByUserId;

    @Column(name = "deleted_by_user_id") // Añadido
    private UUID deletedByUserId;

    @Generated(event = EventType.INSERT) // Añadido @Generated
    @Column(name = "created_by_role_id", updatable = false, insertable = false) // Añadido
    private UUID createdByRoleId;

    @Generated(event = {EventType.INSERT, EventType.UPDATE}) // Añadido @Generated
    @Column(name = "updated_by_role_id", insertable = false) // Añadido
    private UUID updatedByRoleId;

    @Generated(event = {EventType.INSERT, EventType.UPDATE}) // Añadido @Generated
    @Column(name = "deleted_by_role_id", insertable = false) // Añadido
    private UUID deletedByRoleId;
}
