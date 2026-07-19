package com.posapi.infrastructure.adapter.input.rest.posterminal.mapper;

import com.posapi.domain.model.posterminal.PosTerminal;
import com.posapi.infrastructure.adapter.input.rest.posterminal.dto.PosTerminalRequest;
import com.posapi.infrastructure.adapter.input.rest.posterminal.dto.PosTerminalResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal; // Importar BigDecimal si es necesario (no en este caso, pero buena práctica)
import java.time.Instant;
import java.util.UUID;

@Component
public class PosTerminalRestMapper {

    public PosTerminal toDomain(PosTerminalRequest request) {
        if (request == null) {
            return null;
        }
        return PosTerminal.builder()
                .name(request.name())
                .location(request.location())
                .isActive(request.isActive())
                .build();
    }

    public PosTerminalResponse toResponse(PosTerminal posTerminal, String createdByName, String updatedByName, String deletedByName) {
        if (posTerminal == null) {
            return null;
        }
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
