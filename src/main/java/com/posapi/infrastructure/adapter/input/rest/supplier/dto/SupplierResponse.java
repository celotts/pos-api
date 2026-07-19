package com.posapi.infrastructure.adapter.input.rest.supplier.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posapi.domain.model.supplier.Supplier; // Importar la entidad de dominio Supplier

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SupplierResponse(
        UUID id,
        String rfc,
        String businessName,
        String taxRegimen,
        String contactEmail,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt, // Añadido para auditoría
        UUID createdByUserId, // Añadido para auditoría
        UUID updatedByUserId, // Añadido para auditoría
        UUID deletedByUserId, // Añadido para auditoría
        UUID createdByUserRoleId, // Añadido para auditoría
        UUID updatedByUserRoleId, // Añadido para auditoría
        UUID deletedByUserRoleId, // Añadido para auditoría
        String createdByName, // Añadido para nombres de auditoría
        String updatedByName, // Añadido para nombres de auditoría
        String deletedByName  // Añadido para nombres de auditoría
) {
    // Método estático para mapear desde el dominio a la respuesta DTO
    public static SupplierResponse fromDomain(Supplier supplier, String createdByName, String updatedByName, String deletedByName) {
        return new SupplierResponse(
                supplier.getId(),
                supplier.getRfc(),
                supplier.getBusinessName(),
                supplier.getTaxRegimen(),
                supplier.getContactEmail(),
                supplier.getCreatedAt(),
                supplier.getUpdatedAt(),
                supplier.getDeletedAt(),
                supplier.getCreatedByUserId(),
                supplier.getUpdatedByUserId(),
                supplier.getDeletedByUserId(),
                supplier.getCreatedByUserRoleId(),
                supplier.getUpdatedByUserRoleId(),
                supplier.getDeletedByUserRoleId(),
                createdByName,
                updatedByName,
                deletedByName
        );
    }
}
