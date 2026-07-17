package com.posapi.infrastructure.adapter.output.persistence.mapper.tax;

import com.posapi.domain.model.tax.Tax;
import com.posapi.domain.model.tax.TaxEnum;
import com.posapi.infrastructure.adapter.output.persistence.entity.tax.TaxEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TaxPersistenceMapper {

    public TaxEntity toEntity(Tax domain) {
        if (domain == null) {
            return null;
        }
        return TaxEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .percentage(domain.getPercentage())
                .taxType(TaxEntity.TaxType.valueOf(domain.getTaxType().name()))
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

    public Tax toDomain(TaxEntity entity) {
        if (entity == null) {
            return null;
        }
        return Tax.builder()
                .id(entity.getId())
                .name(entity.getName())
                .percentage(entity.getPercentage())
                .taxType(TaxEnum.valueOf(entity.getTaxType().name()))
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
