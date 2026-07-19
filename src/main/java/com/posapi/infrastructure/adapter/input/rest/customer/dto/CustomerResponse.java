package com.posapi.infrastructure.adapter.input.rest.customer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posapi.domain.model.customer.Customer; // Solo se necesita Customer

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomerResponse(
        UUID id,
        String fullName,
        String email,
        String phoneNumber,
        String address,
        String rfc,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,

        // Campos de auditoría de usuario
        UUID createdByUserId,
        UUID updatedByUserId,
        UUID deletedByUserId,

        // Campos de auditoría de rol (AÑADIDOS)
        UUID createdByUserRoleId,
        UUID updatedByUserRoleId,
        UUID deletedByUserRoleId,

        // Nombres de usuario para auditoría
        String createdByName,
        String updatedByName,
        String deletedByName
) {
    public static CustomerResponse fromDomain(Customer customer, String createdByName, String updatedByName, String deletedByName) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getAddress(),
                customer.getRfc(),
                customer.getCreatedAt(),
                customer.getUpdatedAt(),
                customer.getDeletedAt(),
                customer.getCreatedByUserId(), // CORREGIDO
                customer.getUpdatedByUserId(),
                customer.getDeletedByUserId(),
                customer.getCreatedByUserRoleId(), // AÑADIDO
                customer.getUpdatedByUserRoleId(), // AÑADIDO
                customer.getDeletedByUserRoleId(), // AÑADIDO
                createdByName,
                updatedByName,
                deletedByName
        );
    }


}
