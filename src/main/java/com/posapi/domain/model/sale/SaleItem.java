package com.posapi.domain.model.sale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SaleItem {
    private UUID id;
    private UUID saleId;
    private UUID productId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID deletedByUserId;
    private UUID createdByUserRoleId;
    private UUID updatedByUserRoleId;
    private UUID deletedByUserRoleId;

    public static SaleItem createNew(
            UUID saleId, UUID productId, BigDecimal quantity, BigDecimal unitPrice,
            UUID currentUserId, UUID currentUserRoleId) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0 || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity must be positive and unit price cannot be negative.");
        }
        return SaleItem.builder()
                .id(UUID.randomUUID())
                .saleId(saleId)
                .productId(productId)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .subtotal(quantity.multiply(unitPrice))
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByUserRoleId(currentUserRoleId)
                .build();
    }

    public void updateDetails(BigDecimal newQuantity, BigDecimal newUnitPrice, UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (newQuantity != null && newQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        if (newUnitPrice != null && newUnitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative.");
        }

        this.quantity = newQuantity != null ? newQuantity : this.quantity;
        this.unitPrice = newUnitPrice != null ? newUnitPrice : this.unitPrice;
        this.subtotal = this.quantity.multiply(this.unitPrice);
        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByUserRoleId = updatedByUserRoleId;
    }

    public void markAsDeleted(UUID deletedByUserId, UUID deletedByUserRoleId) {
        if (this.deletedAt == null) {
            this.deletedAt = Instant.now();
            this.deletedByUserId = deletedByUserId;
            this.deletedByUserRoleId = deletedByUserRoleId;
        }
    }
}
