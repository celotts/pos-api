package com.posapi.infrastructure.adapter.input.rest.tax.mapper;

import com.posapi.domain.model.tax.Tax;
import com.posapi.infrastructure.adapter.input.rest.tax.dto.TaxRequest;
import com.posapi.infrastructure.adapter.input.rest.tax.dto.TaxResponse;
import org.springframework.stereotype.Component;

@Component
public class TaxRestMapper {

    public Tax toDomain(TaxRequest dto) {
        return Tax.builder()
                .name(dto.name())
                .percentage(dto.percentage())
                .taxType(dto.taxType())
                .build();
    }

    public TaxResponse toResponse(Tax domain, String createdByName, String updatedByName) {
        return new TaxResponse(
                domain.getId(),
                domain.getName(),
                domain.getPercentage(),
                domain.getTaxType(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                createdByName,
                updatedByName
        );
    }
}
