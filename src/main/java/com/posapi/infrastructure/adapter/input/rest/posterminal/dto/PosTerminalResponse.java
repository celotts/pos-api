package com.posapi.infrastructure.adapter.input.rest.posterminal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posapi.domain.model.posterminal.PosTerminal;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
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
        return PosTerminalResponse.builder()
                .id(posTerminal.getId())
                .name(posTerminal.getName())
                .location(posTerminal.getLocation())
                .isActive(posTerminal.getIsActive())
                .createdAt(posTerminal.getCreatedAt())
                .updatedAt(posTerminal.getUpdatedAt())
                .deletedAt(posTerminal.getDeletedAt())
                .createdByUserId(posTerminal.getCreatedByUserId())
                .updatedByUserId(posTerminal.getUpdatedByUserId())
                .deletedByUserId(posTerminal.getDeletedByUserId())
                .createdByUserRoleId(posTerminal.getCreatedByUserRoleId())
                .updatedByUserRoleId(posTerminal.getUpdatedByUserRoleId())
                .deletedByUserRoleId(posTerminal.getDeletedByUserRoleId())
                .createdByName(createdByName)
                .updatedByName(updatedByName)
                .deletedByName(deletedByName)
                .build();
    }
}
