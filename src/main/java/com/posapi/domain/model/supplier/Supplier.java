package com.posapi.domain.model.supplier;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class Supplier {
    private UUID id;
    private String rfc;
    private String businessName;
    private String taxRegimen;
    private String contactEmail;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt; // <-- AÑADIDO
    private UUID createdBy;
    private UUID updatedBy;
    private UUID deletedBy;   // <-- AÑADIDO
}
