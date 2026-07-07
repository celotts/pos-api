package com.posapi.infrastructure.adapter.input.rest.supplier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierRequest(
    @NotBlank @Size(min = 12, max = 13)
    String rfc,
    @NotBlank
    String businessName,
    @NotBlank
    String taxRegimen,
    @Email
    String contactEmail
) {
}
