package com.posapi.infrastructure.adapter.input.rest.shift.mapper;

import com.posapi.domain.model.shift.Shift;
import com.posapi.infrastructure.adapter.input.rest.shift.dto.ShiftRequest;
import com.posapi.infrastructure.adapter.input.rest.shift.dto.ShiftResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
public class ShiftRestMapper {

    public Shift toDomain(ShiftRequest request) {
        if (request == null) {
            return null;
        }
        return Shift.builder()
                .userId(request.userId())
                .posTerminalId(request.posTerminalId())
                .startingCash(request.startingCash())
                .status(request.status())
                .build();
    }

    public ShiftResponse toResponse(Shift shift, String createdByName, String updatedByName, String deletedByName) {
        if (shift == null) {
            return null;
        }
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
