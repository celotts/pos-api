package com.posapi.infrastructure.adapter.input.rest.tax.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posapi.domain.model.tax.Tax;
import com.posapi.domain.model.tax.TaxEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaxResponse(
    UUID id,
    String name,
    BigDecimal percentage,
    TaxEnum taxType,
    Instant createdAt,
    Instant updatedAt,
    String createdByName,
    String updatedByName
) {
    public static TaxResponse fromDomain(Tax tax, String createdByName, String updatedByName) {
        // 🛡️ World-Class: Implement the fromDomain method to correctly map the domain model
        // and enriched data to the DTO.
        return new TaxResponse(
                tax.getId(),
                tax.getName(),
                tax.getPercentage(),
                tax.getTaxType(),
                tax.getCreatedAt(),
                tax.getUpdatedAt(),
                createdByName,
                updatedByName
        );
    }
}
