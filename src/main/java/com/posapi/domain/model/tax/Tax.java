package com.posapi.domain.model.tax;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class Tax {
    private UUID id;
    private String name;
    private BigDecimal percentage;
    private String taxType; // Usamos String para el tipo en el dominio
}
