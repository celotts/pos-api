package com.posapi.infrastructure.adapter.input.rest.product.dto;

import com.posapi.domain.model.product.Product; // Importar el modelo de dominio Product
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull; // Importar NotNull de Jakarta Validation

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    @NotNull
    private UUID id;
    @NotNull
    private String sku;
    @NotNull
    private String name;
    private String description; // Puede ser null
    @NotNull
    private BigDecimal purchasePrice;
    @NotNull
    private BigDecimal salePrice;
    @NotNull
    private BigDecimal currentStock;
    private UUID taxId; // Puede ser null
    private UUID supplierId; // Puede ser null
    @NotNull
    private Instant createdAt;
    @NotNull
    private Instant updatedAt;
    private Instant deletedAt; // Puede ser null
    private UUID createdByUserId; // Puede ser null
    private UUID updatedByUserId; // Puede ser null
    private UUID deletedByUserId; // Puede ser null

    // Método estático para mapear desde el dominio Product a ProductResponse
    @NotNull // Indica que este método siempre devuelve un ProductResponse no nulo
    public static ProductResponse fromProduct(@NotNull Product product) {
        // Simplificamos las comprobaciones de nulidad para los campos @NonNull en Product.java
        return ProductResponse.builder()
                .id(product.getId()) // Simplificado
                .sku(product.getSku()) // Simplificado
                .name(product.getName()) // Simplificado
                .description(product.getDescription()) // Puede ser null
                .purchasePrice(product.getPurchasePrice()) // Simplificado
                .salePrice(product.getSalePrice()) // Simplificado
                .currentStock(product.getCurrentStock()) // Simplificado
                .taxId(product.getTaxId()) // Puede ser null
                .supplierId(product.getSupplierId()) // Puede ser null
                .createdAt(product.getCreatedAt()) // Simplificado
                .updatedAt(product.getUpdatedAt()) // Simplificado
                .deletedAt(product.getDeletedAt()) // Puede ser null
                .createdByUserId(product.getCreatedByUserId()) // Puede ser null
                .updatedByUserId(product.getUpdatedByUserId()) // Puede ser null
                .deletedByUserId(product.getDeletedByUserId()) // Puede ser null
                .build();
    }
}
