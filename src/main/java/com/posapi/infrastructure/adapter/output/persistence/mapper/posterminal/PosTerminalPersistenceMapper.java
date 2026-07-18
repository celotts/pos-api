package com.posapi.infrastructure.adapter.output.persistence.mapper.posterminal;

import com.posapi.domain.model.posterminal.PosTerminal;
import com.posapi.infrastructure.adapter.output.persistence.entity.posterminal.PosTerminalEntity;
import org.springframework.stereotype.Component;

@Component
public class PosTerminalPersistenceMapper {

    public PosTerminalEntity toEntity(PosTerminal domain) {
        if (domain == null) {
            return null;
        }
        return PosTerminalEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .location(domain.getLocation())
                .isActive(domain.getIsActive())
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

    public PosTerminal toDomain(PosTerminalEntity entity) {
        if (entity == null) {
            return null;
        }
        return PosTerminal.builder()
                .id(entity.getId())
                .name(entity.getName())
                .location(entity.getLocation())
                .isActive(entity.getIsActive())
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
