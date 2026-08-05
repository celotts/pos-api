package com.posapi.domain.model.cashaccount;

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
public class CashAccount extends BaseModel {
    private String name;
    private CashAccountType accountType;
    private BigDecimal currentBalance;
    private String currency;

    public static CashAccount createNew(
            String name, CashAccountType accountType, BigDecimal initialBalance, String currency,
            UUID currentUserId, UUID currentUserRoleId) {
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        CashAccount account = new CashAccount();
        account.setId(UUID.randomUUID());
        account.setName(name);
        account.setAccountType(accountType);
        account.setCurrentBalance(initialBalance);
        account.setCurrency(currency);
        account.setCreatedAt(Instant.now());
        account.setCreatedByUserId(currentUserId);
        account.setCreatedByUserRoleId(currentUserRoleId);
        return account;
    }

    public void deposit(BigDecimal amount, UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        this.currentBalance = this.currentBalance.add(amount);
        this.setUpdatedAt(Instant.now());
        this.setUpdatedByUserId(updatedByUserId);
        this.setUpdatedByUserRoleId(updatedByUserRoleId);
    }

    public void withdraw(BigDecimal amount, UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (this.currentBalance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds for withdrawal.");
        }
        this.currentBalance = this.currentBalance.subtract(amount);
        this.setUpdatedAt(Instant.now());
        this.setUpdatedByUserId(updatedByUserId);
        this.setUpdatedByUserRoleId(updatedByUserRoleId);
    }

    public void updateName(String newName, UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Account name cannot be null or empty.");
        }
        this.name = newName;
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
