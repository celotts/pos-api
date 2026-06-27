package com.posapi.infrastructure.adapter.output.persistence.entity.supplier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "suppliers")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    // Add other relevant supplier fields here (e.g., contactInfo, address)

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}