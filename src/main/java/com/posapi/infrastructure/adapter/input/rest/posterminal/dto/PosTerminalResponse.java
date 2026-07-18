package com.posapi.infrastructure.adapter.input.rest.posterminal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posapi.domain.model.posterminal.PosTerminal; // Importar el modelo de dominio

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PosTerminalResponse(
    UUID id,
    String name,
    String location,
    Boolean isActive,
    Instant createdAt,
    Instant updatedAt,
    Instant deletedAt,
    UUID createdByUserId,
    UUID updatedByUserId,
    UUID deletedByUserId,
    UUID createdByUserRoleId,
    UUID updatedByUserRoleId,
    UUID deletedByUserRoleId,
    String createdByName,
    String updatedByName,
    String deletedByName
) {
    public static PosTerminalResponse fromDomain(PosTerminal posTerminal, String createdByName, String updatedByName, String deletedByName) {
        return new PosTerminalResponse(
                posTerminal.getId(),
                posTerminal.getName(),
                posTerminal.getLocation(),
                posTerminal.getIsActive(),
                posTerminal.getCreatedAt(),
                posTerminal.getUpdatedAt(),
                posTerminal.getDeletedAt(),
                posTerminal.getCreatedByUserId(),
                posTerminal.getUpdatedByUserId(),
                posTerminal.getDeletedByUserId(),
                posTerminal.getCreatedByUserRoleId(),
                posTerminal.getUpdatedByUserRoleId(),
                posTerminal.getDeletedByUserRoleId(),
                createdByName,
                updatedByName,
                deletedByName
        );
    }
}
