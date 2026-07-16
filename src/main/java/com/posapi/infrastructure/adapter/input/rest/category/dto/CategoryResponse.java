package com.posapi.infrastructure.adapter.input.rest.category.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posapi.domain.model.category.Category; // Necesario para el método fromDomain

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryResponse(
        UUID id,
        String name,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt, // Añadido para consistencia
        UUID createdByUserId, // Añadido para auditoría
        UUID updatedByUserId, // Añadido para auditoría
        UUID deletedByUserId, // Añadido para auditoría
        String createdByName,
        String updatedByName,
        String deletedByName // Añadido para consistencia
) {
    public static CategoryResponse fromDomain(Category category, String createdByName, String updatedByName, String deletedByName) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                category.getDeletedAt(), // Usar el campo de la entidad
                category.getCreatedByUserId(), // Usar el campo de la entidad
                category.getUpdatedByUserId(), // Usar el campo de la entidad
                category.getDeletedByUserId(), // Usar el campo de la entidad
                createdByName,
                updatedByName,
                deletedByName
        );
    }

    // Método auxiliar para crear un CategoryResponse a partir de otro CategoryResponse (útil en el controlador)
    public static CategoryResponse fromResponse(CategoryResponse response, String createdByName, String updatedByName, String deletedByName) {
        return new CategoryResponse(
                response.id(),
                response.name(),
                response.createdAt(),
                response.updatedAt(),
                response.deletedAt(),
                response.createdByUserId(),
                response.updatedByUserId(),
                response.deletedByUserId(),
                createdByName,
                updatedByName,
                deletedByName
        );
    }


}
