package com.posapi.infrastructure.adapter.output.persistence.mapper.tax;

import com.posapi.domain.model.tax.Tax;
import com.posapi.infrastructure.adapter.output.persistence.entity.tax.TaxEntity;
import org.springframework.stereotype.Component;

@Component
public class TaxPersistenceMapper {

    public TaxEntity toEntity(Tax domain) {
        return TaxEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .percentage(domain.getPercentage())
                .taxType(TaxEntity.TaxType.valueOf(domain.getTaxType()))
                .build();
    }

    public Tax toDomain(TaxEntity entity) {
        return Tax.builder()
                .id(entity.getId())
                .name(entity.getName())
                .percentage(entity.getPercentage())
                .taxType(entity.getTaxType().name())
                .build();
    }
}
