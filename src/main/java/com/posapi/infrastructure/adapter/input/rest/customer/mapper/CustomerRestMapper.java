package com.posapi.infrastructure.adapter.input.rest.customer.mapper;

import com.posapi.domain.model.customer.Customer;
import com.posapi.infrastructure.adapter.input.rest.customer.dto.CustomerRequest;
import com.posapi.infrastructure.adapter.input.rest.customer.dto.CustomerResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component // AÑADIDO: Para que Spring la detecte como un bean
public class CustomerRestMapper {

    public Customer toDomain(CustomerRequest request) {
        if (request == null) {
            return null;
        }
        return Customer.builder()
                .fullName(request.fullName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .address(request.address())
                .rfc(request.rfc())
                .build();
    }

    public CustomerResponse toResponse(Customer customer, String createdByName, String updatedByName, String deletedByName) {
        if (customer == null) {
            return null;
        }
        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getAddress(),
                customer.getRfc(),
                customer.getCreatedAt(),
                customer.getUpdatedAt(),
                customer.getDeletedAt(),
                customer.getCreatedByUserId(),
                customer.getUpdatedByUserId(),
                customer.getDeletedByUserId(),
                customer.getCreatedByUserRoleId(),
                customer.getUpdatedByUserRoleId(),
                customer.getDeletedByUserRoleId(),
                createdByName,
                updatedByName,
                deletedByName
        );
    }
}
