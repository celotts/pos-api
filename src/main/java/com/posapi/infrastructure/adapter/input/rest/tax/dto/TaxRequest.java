package com.posapi.infrastructure.adapter.input.rest.tax.dto;

import com.posapi.domain.model.tax.Tax;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TaxRequest(
    @NotBlank String name,
    @NotNull @DecimalMin("0.0") @DecimalMax("1.0") BigDecimal percentage,
    @NotNull Tax.TaxCategory taxType
) {
}
