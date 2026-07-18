package com.posapi.infrastructure.adapter.output.persistence.mapper.shift;

import com.posapi.domain.model.shift.Shift;
import com.posapi.infrastructure.adapter.output.persistence.entity.shift.ShiftEntity;
import org.springframework.stereotype.Component;

@Component
public class ShiftPersistenceMapper {

    public ShiftEntity toEntity(Shift domain) {
        if (domain == null) {
            return null;
        }
        return ShiftEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .posTerminalId(domain.getPosTerminalId())
                .startTime(domain.getStartTime())
                .endTime(domain.getEndTime())
                .startingCash(domain.getStartingCash())
                .endingCash(domain.getEndingCash())
                .status(ShiftEntity.ShiftStatus.valueOf(domain.getStatus().name()))
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .deletedAt(domain.getDeletedAt())
                .createdByUserId(domain.getCreatedByUserId())
                .updatedByUserId(domain.getUpdatedByUserId())
                .deletedByUserId(domain.getDeletedByUserId())
                .createdByRoleId(domain.getCreatedByUserRoleId())
                .updatedByRoleId(domain.getUpdatedByUserRoleId())
                .deletedByRoleId(domain.getDeletedByUserRoleId())
                .build();
    }

    public Shift toDomain(ShiftEntity entity) {
        if (entity == null) {
            return null;
        }
        return Shift.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .posTerminalId(entity.getPosTerminalId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .startingCash(entity.getStartingCash())
                .endingCash(entity.getEndingCash())
                .status(com.posapi.domain.model.shift.ShiftStatus.valueOf(entity.getStatus().name()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .createdByUserId(entity.getCreatedByUserId())
                .updatedByUserId(entity.getUpdatedByUserId())
                .deletedByUserId(entity.getDeletedByUserId())
                .createdByUserRoleId(entity.getCreatedByRoleId())
                .updatedByUserRoleId(entity.getUpdatedByRoleId())
                .deletedByUserRoleId(entity.getDeletedByRoleId())
                .build();
    }
}
