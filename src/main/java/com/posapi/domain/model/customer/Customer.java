package com.posapi.domain.model.customer;

import com.posapi.domain.model.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends BaseModel {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String rfc;

    public static Customer createNew(
            String fullName, String email, String phoneNumber, String address, String rfc,
            UUID currentUserId, UUID currentUserRoleId) {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFullName(fullName);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        customer.setAddress(address);
        customer.setRfc(rfc);
        customer.setCreatedAt(Instant.now());
        customer.setCreatedByUserId(currentUserId);
        customer.setCreatedByUserRoleId(currentUserRoleId);
        return customer;
    }

    public void updateDetails(
            String newFullName, String newEmail, String newPhoneNumber, String newAddress, String newRfc,
            UUID updatedByUserId, UUID updatedByUserRoleId) {

        this.fullName = newFullName != null ? newFullName : this.fullName;
        this.email = newEmail != null ? newEmail : this.email;
        this.phoneNumber = newPhoneNumber != null ? newPhoneNumber : this.phoneNumber;
        this.address = newAddress != null ? newAddress : this.address;
        this.rfc = newRfc != null ? newRfc : this.rfc;

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
