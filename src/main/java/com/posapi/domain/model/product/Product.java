package com.posapi.domain.model.product;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private UUID id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private BigDecimal currentStock;
    private UUID categoryId;
    private UUID taxId;
    private UUID supplierId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID deletedByUserId;
    private UUID createdByUserRoleId;
    private UUID updatedByUserRoleId;
    private UUID deletedByUserRoleId;

    // Método estático para crear un nuevo producto
    public static Product createNew(
            String sku, String name, String description, BigDecimal purchasePrice, BigDecimal salePrice,
            BigDecimal initialStock, UUID categoryId, UUID taxId, UUID supplierId,
            UUID currentUserId, UUID currentUserRoleId) {
        if (purchasePrice.compareTo(BigDecimal.ZERO) < 0 || salePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Prices cannot be negative.");
        }
        if (initialStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial stock cannot be negative.");
        }

        return Product.builder()
                .id(UUID.randomUUID())
                .sku(sku)
                .name(name)
                .description(description)
                .purchasePrice(purchasePrice)
                .salePrice(salePrice)
                .currentStock(initialStock)
                .categoryId(categoryId)
                .taxId(taxId)
                .supplierId(supplierId)
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByUserRoleId(currentUserRoleId)
                .build();
    }

    // Método de dominio para actualizar los detalles del producto
    public void updateDetails(
            String newName, String newDescription, BigDecimal newPurchasePrice, BigDecimal newSalePrice,
            UUID newCategoryId, UUID newTaxId, UUID newSupplierId,
            UUID updatedByUserId, UUID updatedByUserRoleId) {

        if (newPurchasePrice != null && newPurchasePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Purchase price cannot be negative.");
        }
        if (newSalePrice != null && newSalePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Sale price cannot be negative.");
        }

        this.name = newName != null ? newName : this.name;
        this.description = newDescription != null ? newDescription : this.description;
        this.purchasePrice = newPurchasePrice != null ? newPurchasePrice : this.purchasePrice;
        this.salePrice = newSalePrice != null ? newSalePrice : this.salePrice;
        this.categoryId = newCategoryId != null ? newCategoryId : this.categoryId;
        this.taxId = newTaxId != null ? newTaxId : this.taxId;
        this.supplierId = newSupplierId != null ? newSupplierId : this.supplierId;

        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByUserRoleId = updatedByUserRoleId;
    }

    // Método de dominio para aumentar el stock
    public void increaseStock(BigDecimal quantity, UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity to increase cannot be negative.");
        }
        this.currentStock = this.currentStock.add(quantity);
        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByUserRoleId = updatedByUserRoleId;
    }

    // Método de dominio para disminuir el stock
    public void decreaseStock(BigDecimal quantity, UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity to decrease cannot be negative.");
        }
        if (this.currentStock.subtract(quantity).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Stock cannot go below zero.");
        }
        this.currentStock = this.currentStock.subtract(quantity);
        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByUserRoleId = updatedByUserRoleId;
    }

    // Método de dominio para borrado lógico
    public void markAsDeleted(UUID deletedByUserId, UUID deletedByUserRoleId) {
        if (this.deletedAt == null) {
            this.deletedAt = Instant.now();
            this.deletedByUserId = deletedByUserId;
            this.deletedByUserRoleId = deletedByUserRoleId;
        }
    }
}
