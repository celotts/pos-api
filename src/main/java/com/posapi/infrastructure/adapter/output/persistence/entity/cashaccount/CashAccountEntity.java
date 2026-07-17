package com.posapi.infrastructure.adapter.output.persistence.entity.cashaccount;

import com.posapi.domain.model.cashaccount.CashAccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.math.BigDecimal; // Importar BigDecimal
import java.time.Instant;
import java.util.UUID;

// AÑADIDO: Importación para CashAccountType (si está en el mismo paquete, no es necesaria la importación explícita)
// Si CashAccountType está en otro paquete, asegúrate de importarlo.
// Por ahora, asumo que está en el mismo paquete o en un paquete accesible.

@Entity
@Table(name = "cash_accounts")
@Data
@Builder(toBuilder = true) // Añadido toBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CashAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING) // CORREGIDO: Usar EnumType.STRING para mapear el ENUM de la DB
    @Column(name = "account_type", nullable = false)
    private CashAccountType accountType; // CORREGIDO: Cambiado de String a CashAccountType

    @Column(name = "current_balance", nullable = false, precision = 18, scale = 2) // Añadido precision y scale
    private BigDecimal currentBalance; // CORREGIDO: Cambiado de double a BigDecimal

    @Column(nullable = false, length = 3) // Añadido length para currency
    private String currency;

    @Generated(event = EventType.INSERT) // CORREGIDO: Añadido @Generated
    @Column(name = "created_at", updatable = false, insertable = false) // CORREGIDO: insertable = false
    private Instant createdAt;

    @Generated(event = {EventType.INSERT, EventType.UPDATE}) // CORREGIDO: Añadido @Generated
    @Column(name = "updated_at", insertable = false) // CORREGIDO: insertable = false
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "created_by_user_id", updatable = false)
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @Column(name = "deleted_by_user_id")
    private UUID deletedByUserId;

    @Generated(event = EventType.INSERT) // CORREGIDO: Añadido @Generated
    @Column(name = "created_by_role_id", updatable = false, insertable = false) // CORREGIDO: insertable = false
    private UUID createdByRoleId; // CORREGIDO: Renombrado a createdByRoleId

    @Generated(event = {EventType.INSERT, EventType.UPDATE}) // CORREGIDO: Añadido @Generated
    @Column(name = "updated_by_role_id", insertable = false) // CORREGIDO: Añadido
    private UUID updatedByRoleId; // CORREGIDO: Añadido

    @Generated(event = {EventType.INSERT, EventType.UPDATE}) // CORREGIDO: Añadido @Generated
    @Column(name = "deleted_by_role_id", insertable = false) // CORREGIDO: Añadido
    private UUID deletedByRoleId; // CORREGIDO: Añadido

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
