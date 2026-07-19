package com.posapi.domain.model.cashaccount;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor; // Añadido
import lombok.NoArgsConstructor; // Añadido

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// AÑADIDO: Importación para CashAccountType
import com.posapi.domain.model.cashaccount.CashAccountType;

@Data
@Builder(toBuilder = true) // Añadido toBuilder
@NoArgsConstructor // Añadido
@AllArgsConstructor // Añadido
public class CashAccount {
    private UUID id;
    private String name;
    private CashAccountType accountType;
    private BigDecimal currentBalance;
    private String currency;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID deletedByUserId;
    private UUID createdByUserRoleId;
    private UUID updatedByUserRoleId;
    private UUID deletedByUserRoleId;

    // Método estático para crear una nueva cuenta de efectivo
    public static CashAccount createNew(
            String name, CashAccountType accountType, BigDecimal initialBalance, String currency,
            UUID currentUserId, UUID currentUserRoleId) {
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        return CashAccount.builder()
                .id(UUID.randomUUID())
                .name(name)
                .accountType(accountType)
                .currentBalance(initialBalance)
                .currency(currency)
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByUserRoleId(currentUserRoleId)
                .build();
    }

    // Método de dominio para depositar fondos
    public void deposit(BigDecimal amount, UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        this.currentBalance = this.currentBalance.add(amount);
        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByUserRoleId = updatedByUserRoleId;
    }

    // Método de dominio para retirar fondos
    public void withdraw(BigDecimal amount, UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (this.currentBalance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds for withdrawal.");
        }
        this.currentBalance = this.currentBalance.subtract(amount);
        this.updatedAt = Instant.now();
        this.updatedByUserId = updatedByUserId;
        this.updatedByUserRoleId = updatedByUserRoleId;
    }

    // Método de dominio para actualizar el nombre de la cuenta
    public void updateName(String newName, UUID updatedByUserId, UUID updatedByUserRoleId) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Account name cannot be null or empty.");
        }
        this.name = newName;
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
