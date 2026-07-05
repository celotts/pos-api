package com.posapi.domain.model.supplier;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class Supplier {
    private UUID id;
    private String rfc;
    private String businessName;
    private String taxRegimen;
    private String contactEmail;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdBy;
    private UUID updatedBy;
    private UUID deletedBy;
}
