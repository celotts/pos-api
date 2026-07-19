package com.posapi.infrastructure.adapter.input.rest.shift.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posapi.domain.model.shift.Shift; // Importar el modelo de dominio
import com.posapi.domain.model.shift.ShiftStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ShiftResponse(
    UUID id,
    UUID userId,
    UUID posTerminalId,
    Instant startTime,
    Instant endTime,
    BigDecimal startingCash,
    BigDecimal endingCash,
    ShiftStatus status,
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
    public static ShiftResponse fromDomain(Shift shift, String createdByName, String updatedByName, String deletedByName) {
        return new ShiftResponse(
                shift.getId(),
                shift.getUserId(),
                shift.getPosTerminalId(),
                shift.getStartTime(),
                shift.getEndTime(),
                shift.getStartingCash(),
                shift.getEndingCash(),
                shift.getStatus(),
                shift.getCreatedAt(),
                shift.getUpdatedAt(),
                shift.getDeletedAt(),
                shift.getCreatedByUserId(),
                shift.getUpdatedByUserId(),
                shift.getDeletedByUserId(),
                shift.getCreatedByUserRoleId(),
                shift.getUpdatedByUserRoleId(),
                shift.getDeletedByUserRoleId(),
                createdByName,
                updatedByName,
                deletedByName
        );
    }
}
