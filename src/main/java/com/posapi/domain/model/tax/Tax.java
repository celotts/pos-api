package com.posapi.domain.model.tax;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor; // Añadido
import lombok.NoArgsConstructor; // Añadido

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true) // Añadido toBuilder
@NoArgsConstructor // Añadido
@AllArgsConstructor // Añadido
public class Tax {
    private UUID id;
    private String name;
    private BigDecimal percentage;
    private TaxEnum taxType; // Usando TaxEnum del dominio
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt; // Añadido para auditoría
    private UUID createdByUserId; // Renombrado para consistencia
    private UUID updatedByUserId; // Renombrado para consistencia
    private UUID deletedByUserId; // Añadido para auditoría
    private UUID createdByUserRoleId; // Añadido para auditoría
    private UUID updatedByUserRoleId; // Añadido para auditoría
    private UUID deletedByUserRoleId; // Añadido para auditoría

    // Método estático para crear un nuevo impuesto
    public static Tax createNew(
            String name, BigDecimal percentage, TaxEnum taxType,
            UUID currentUserId, UUID currentUserRoleId) {
        if (percentage.compareTo(BigDecimal.ZERO) < 0 || percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100.");
        }
        return Tax.builder()
                .id(UUID.randomUUID())
                .name(name)
                .percentage(percentage)
                .taxType(taxType)
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByUserRoleId(currentUserRoleId)
                .build();
    }

    // Método de dominio para actualizar los detalles del impuesto
    public void updateDetails(
            String newName, BigDecimal newPercentage, TaxEnum newTaxType,
            UUID updatedByUserId, UUID updatedByUserRoleId) {

        if (newPercentage != null && (newPercentage.compareTo(BigDecimal.ZERO) < 0 || newPercentage.compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100.");
        }

        this.name = newName != null ? newName : this.name;
        this.percentage = newPercentage != null ? newPercentage : this.percentage;
        this.taxType = newTaxType != null ? newTaxType : this.taxType;

        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByUserRoleId = updatedByUserRoleId;
    }

    // Método de dominio para borrado lógico
    public void markAsDeleted(UUID deletedByUserId, UUID deletedByUserRoleId) {
        if (this.deletedAt == null) {
            this.deletedAt = Instant.now();
            this.deletedByUserId = deletedByUserId;
            this.deletedByUserRoleId = deletedByUserRoleId;
        }
    }


}
