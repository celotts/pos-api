package com.posapi.infrastructure.adapter.input.rest.tax.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.posapi.domain.model.tax.Tax;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaxResponse(
    UUID id,
    String name,
    BigDecimal percentage,
    Tax.TaxCategory taxType,
    Instant createdAt,
    Instant updatedAt,
    String createdByName,
    String updatedByName
) {
}
