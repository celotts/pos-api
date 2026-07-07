package com.posapi.infrastructure.adapter.output.persistence.mapper.tax;

import com.posapi.domain.model.tax.Tax;
import com.posapi.infrastructure.adapter.output.persistence.entity.tax.TaxEntity;
import org.springframework.stereotype.Component;

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
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
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
                .taxType(Tax.TaxCategory.valueOf(entity.getTaxType().name()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
