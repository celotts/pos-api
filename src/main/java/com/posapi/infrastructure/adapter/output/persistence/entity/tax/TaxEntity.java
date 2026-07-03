package com.posapi.infrastructure.adapter.output.persistence.entity.tax;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "taxes")
public class TaxEntity {

    @Id
    private UUID id;

    private String name;

    private BigDecimal percentage;

    @Enumerated(EnumType.STRING)
    private TaxType taxType;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    // Enum para el tipo de impuesto, específico de la persistencia
    public enum TaxType {
        IVA, IEPS, ISR
    }
}
