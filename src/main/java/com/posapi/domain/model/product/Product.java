package com.posapi.domain.model.product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.NonNull; // Importar NonNull de Lombok

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @NonNull
    private UUID id;
    @NonNull
    private String sku;
    @NonNull
    private String name;
    private String description; // Puede ser null
    @NonNull
    private BigDecimal purchasePrice;
    @NonNull
    private BigDecimal salePrice;
    @NonNull
    private BigDecimal currentStock;
    private UUID taxId; // Referencia al ID del impuesto (puede ser null)
    private UUID supplierId; // Referencia al ID del proveedor (puede ser null)
    @NonNull
    private Instant createdAt;
    @NonNull
    private Instant updatedAt;
    private Instant deletedAt; // Puede ser null
    private UUID createdByUserId; // Puede ser null
    private UUID updatedByUserId; // Puede ser null
    private UUID deletedByUserId; // Puede ser null

    // Métodos de dominio específicos para Product podrían ir aquí
    // Por ejemplo, para actualizar stock, calcular precios con impuestos, etc.
}
