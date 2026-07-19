package com.posapi.domain.model.customer;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor; // Añadido
import lombok.NoArgsConstructor; // Añadido

import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true) // Añadido toBuilder
@NoArgsConstructor // Añadido
@AllArgsConstructor // Añadido
public class Customer {
    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String rfc;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private UUID deletedByUserId;
    private UUID createdByUserRoleId;
    private UUID updatedByUserRoleId;
    private UUID deletedByUserRoleId;

    // Método estático para crear un nuevo cliente
    public static Customer createNew(
            String fullName, String email, String phoneNumber, String address, String rfc,
            UUID currentUserId, UUID currentUserRoleId) {
        return Customer.builder()
                .id(UUID.randomUUID())
                .fullName(fullName)
                .email(email)
                .phoneNumber(phoneNumber)
                .address(address)
                .rfc(rfc)
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByUserRoleId(currentUserRoleId)
                .build();
    }

    // Método de dominio para actualizar los detalles del cliente
    public void updateDetails(
            String newFullName, String newEmail, String newPhoneNumber, String newAddress, String newRfc,
            UUID updatedByUserId, UUID updatedByUserRoleId) {

        this.fullName = newFullName != null ? newFullName : this.fullName;
        this.email = newEmail != null ? newEmail : this.email;
        this.phoneNumber = newPhoneNumber != null ? newPhoneNumber : this.phoneNumber;
        this.address = newAddress != null ? newAddress : this.address;
        this.rfc = newRfc != null ? newRfc : this.rfc;

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
