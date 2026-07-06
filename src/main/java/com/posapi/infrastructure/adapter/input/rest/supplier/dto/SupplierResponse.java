package com.posapi.infrastructure.adapter.input.rest.supplier.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SupplierResponse(
    UUID id,
    String rfc,
    String businessName,
    String taxRegimen,
    String contactEmail,
    Instant createdAt,
    Instant updatedAt,
    String createdByName,
    String updatedByName
) {
}
