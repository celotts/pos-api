package com.posapi.infrastructure.adapter.output.persistence.entity.tax;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated; // Importar Generated
import org.hibernate.generator.EventType; // Importar EventType

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true) // Añadido toBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "taxes")
public class TaxEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true) // Añadido nullable y unique
    private String name;

    @Column(nullable = false, precision = 5, scale = 2) // Añadido precision y scale
    private BigDecimal percentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "tax_type", nullable = false) // Mapear a tax_type
    private TaxType taxType;

    @Generated(event = EventType.INSERT) // Añadido
    @Column(name = "created_at", updatable = false, insertable = false) // Corregido insertable
    private Instant createdAt;

    @Generated(event = {EventType.INSERT, EventType.UPDATE}) // Añadido
    @Column(name = "updated_at", insertable = false) // Corregido insertable
    private Instant updatedAt;

    @Column(name = "deleted_at") // AÑADIDO
    private Instant deletedAt;

    @Column(name = "created_by_user_id", updatable = false) // Renombrado y añadido updatable
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id") // Renombrado
    private UUID updatedByUserId;

    @Column(name = "deleted_by_user_id") // AÑADIDO
    private UUID deletedByUserId;

    @Generated(event = EventType.INSERT) // Añadido
    @Column(name = "created_by_role_id", updatable = false, insertable = false) // AÑADIDO
    private UUID createdByRoleId;

    @Generated(event = {EventType.INSERT, EventType.UPDATE}) // Añadido
    @Column(name = "updated_by_role_id", insertable = false) // AÑADIDO
    private UUID updatedByRoleId;

    @Generated(event = {EventType.INSERT, EventType.UPDATE}) // Añadido
    @Column(name = "deleted_by_role_id", insertable = false) // AÑADIDO
    private UUID deletedByRoleId;

    // Enum para el tipo de impuesto, específico de la persistencia
    public enum TaxType {
        IVA, IEPS, ISR
    }
}
