package com.posapi.domain.model.tax;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class Tax {
    private UUID id;
    private String name;
    private BigDecimal percentage;
    private TaxCategory taxType;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdBy;
    private UUID updatedBy;

    public enum TaxCategory {
        IVA, IEPS, ISR
    }
}
