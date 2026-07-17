package com.posapi.domain.model.supplier;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor; // Añadido
import lombok.NoArgsConstructor; // Añadido

import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true) // Añadido toBuilder
@NoArgsConstructor // Añadido
@AllArgsConstructor // Añadido
public class Supplier {
    private UUID id;
    private String rfc;
    private String businessName;
    private String taxRegimen;
    private String contactEmail;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdByUserId; // Renombrado para consistencia
    private UUID updatedByUserId; // Renombrado para consistencia
    private UUID deletedByUserId; // Renombrado para consistencia
    private UUID createdByUserRoleId; // Añadido para auditoría
    private UUID updatedByUserRoleId; // Añadido para auditoría
    private UUID deletedByUserRoleId; // Añadido para auditoría

    // Método estático para crear un nuevo proveedor
    public static Supplier createNew(
            String rfc, String businessName, String taxRegimen, String contactEmail,
            UUID currentUserId, UUID currentUserRoleId) {
        return Supplier.builder()
                .id(UUID.randomUUID())
                .rfc(rfc)
                .businessName(businessName)
                .taxRegimen(taxRegimen)
                .contactEmail(contactEmail)
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByUserRoleId(currentUserRoleId)
                .build();
    }

    // Método de dominio para actualizar los detalles del proveedor
    public void updateDetails(
            String newRfc, String newBusinessName, String newTaxRegimen, String newContactEmail,
            UUID updatedByUserId, UUID updatedByUserRoleId) {

        this.rfc = newRfc != null ? newRfc : this.rfc;
        this.businessName = newBusinessName != null ? newBusinessName : this.businessName;
        this.taxRegimen = newTaxRegimen != null ? newTaxRegimen : this.taxRegimen;
        this.contactEmail = newContactEmail != null ? newContactEmail : this.contactEmail;

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
