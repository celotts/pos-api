package com.posapi.infrastructure.adapter.output.persistence.entity.tax;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "taxes")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal rate; // e.g., 16.00 for 16%
}