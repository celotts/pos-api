package com.posapi.domain.model.sale;

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
public class Sale {
    private UUID id;
    private UUID customerId;
    private Instant saleDate;
    private BigDecimal totalAmount;
    private BigDecimal totalTaxAmount;
    private BigDecimal discountAmount;
    private SaleStatus status;
    private PaymentStatus paymentStatus;
    private UUID posTerminalId;
    private UUID shiftId;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID deletedByUserId;
    private UUID createdByUserRoleId;
    private UUID updatedByUserRoleId;
    private UUID deletedByUserRoleId;

    // Método estático para crear una nueva venta
    public static Sale createNew(
            UUID customerId, BigDecimal totalAmount, BigDecimal totalTaxAmount, BigDecimal discountAmount,
            UUID posTerminalId, UUID shiftId, UUID currentUserId, UUID currentUserRoleId) {
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0 || totalTaxAmount.compareTo(BigDecimal.ZERO) < 0 || discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amounts cannot be negative.");
        }
        return Sale.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .saleDate(Instant.now())
                .totalAmount(totalAmount)
                .totalTaxAmount(totalTaxAmount)
                .discountAmount(discountAmount)
                .status(SaleStatus.PENDING)
                .paymentStatus(PaymentStatus.UNPAID)
                .posTerminalId(posTerminalId)
                .shiftId(shiftId)
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByUserRoleId(currentUserRoleId)
                .build();
    }

    // Método de dominio para marcar la venta como completada
    public void markAsCompleted(UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (this.status == SaleStatus.PENDING) {
            this.status = SaleStatus.COMPLETED;
            this.updatedAt = Instant.now();
            this.updatedByUserId = updatedByUserId;
            this.updatedByUserRoleId = updatedByUserRoleId;
        } else {
            throw new IllegalStateException("Sale cannot be completed from status: " + this.status);
        }
    }

    // Método de dominio para marcar la venta como cancelada
    public void markAsCancelled(UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (this.status != SaleStatus.COMPLETED) {
            this.status = SaleStatus.CANCELLED;
            this.updatedAt = Instant.now();
            this.updatedByUserId = updatedByUserId;
            this.updatedByUserRoleId = updatedByUserRoleId;
        } else {
            throw new IllegalStateException("Completed sale cannot be cancelled.");
        }
    }

    // Método de dominio para actualizar el estado de pago
    public void updatePaymentStatus(PaymentStatus newPaymentStatus, UUID updatedByUserId, UUID updatedByUserRoleId) {
        this.paymentStatus = newPaymentStatus;
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
