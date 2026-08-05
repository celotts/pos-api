package com.posapi.domain.model.sale;

import com.posapi.domain.model.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Sale extends BaseModel {
    private UUID customerId;
    private Instant saleDate;
    private BigDecimal totalAmount;
    private BigDecimal totalTaxAmount;
    private BigDecimal discountAmount;
    private SaleStatus status;
    private PaymentStatus paymentStatus;
    private UUID posTerminalId;
    private UUID shiftId;

    public static Sale createNew(
            UUID customerId, BigDecimal totalAmount, BigDecimal totalTaxAmount, BigDecimal discountAmount,
            UUID posTerminalId, UUID shiftId, UUID currentUserId, UUID currentUserRoleId) {
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0 || totalTaxAmount.compareTo(BigDecimal.ZERO) < 0 || discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amounts cannot be negative.");
        }
        Sale sale = new Sale();
        sale.setId(UUID.randomUUID());
        sale.setCustomerId(customerId);
        sale.setSaleDate(Instant.now());
        sale.setTotalAmount(totalAmount);
        sale.setTotalTaxAmount(totalTaxAmount);
        sale.setDiscountAmount(discountAmount);
        sale.setStatus(SaleStatus.PENDING);
        sale.setPaymentStatus(PaymentStatus.UNPAID);
        sale.setPosTerminalId(posTerminalId);
        sale.setShiftId(shiftId);
        sale.setCreatedAt(Instant.now());
        sale.setCreatedByUserId(currentUserId);
        sale.setCreatedByUserRoleId(currentUserRoleId);
        return sale;
    }

    public void markAsCompleted(UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (this.status == SaleStatus.PENDING) {
            this.status = SaleStatus.COMPLETED;
            this.setUpdatedAt(Instant.now());
            this.setUpdatedByUserId(updatedByUserId);
            this.setUpdatedByUserRoleId(updatedByUserRoleId);
        } else {
            throw new IllegalStateException("Sale cannot be completed from status: " + this.status);
        }
    }

    public void markAsCancelled(UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (this.status != SaleStatus.COMPLETED) {
            this.status = SaleStatus.CANCELLED;
            this.setUpdatedAt(Instant.now());
            this.setUpdatedByUserId(updatedByUserId);
            this.setUpdatedByUserRoleId(updatedByUserRoleId);
        } else {
            throw new IllegalStateException("Completed sale cannot be cancelled.");
        }
    }

    public void updatePaymentStatus(PaymentStatus newPaymentStatus, UUID updatedByUserId, UUID updatedByUserRoleId) {
        this.paymentStatus = newPaymentStatus;
        this.setUpdatedAt(Instant.now());
        this.setUpdatedByUserId(updatedByUserId);
        this.setUpdatedByUserRoleId(updatedByUserRoleId);
    }

    public void markAsDeleted(UUID deletedByUserId, UUID deletedByUserRoleId) {
        if (this.getDeletedAt() == null) {
            this.setDeletedAt(Instant.now());
            this.setDeletedByUserId(deletedByUserId);
            this.setDeletedByUserRoleId(deletedByUserRoleId);
        }
    }
}
