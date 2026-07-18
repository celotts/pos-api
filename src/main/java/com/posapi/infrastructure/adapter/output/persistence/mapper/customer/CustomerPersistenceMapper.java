package com.posapi.infrastructure.adapter.output.persistence.mapper.customer;

import com.posapi.domain.model.customer.Customer;
import com.posapi.infrastructure.adapter.output.persistence.entity.customer.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceMapper {

    public CustomerEntity toEntity(Customer domain) {
        if (domain == null) {
            return null;
        }
        return CustomerEntity.builder()
                .id(domain.getId())
                .fullName(domain.getFullName())
                .email(domain.getEmail())
                .phoneNumber(domain.getPhoneNumber())
                .address(domain.getAddress())
                .rfc(domain.getRfc())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .deletedAt(domain.getDeletedAt())
                .createdByUserId(domain.getCreatedByUserId())
                .updatedByUserId(domain.getUpdatedByUserId())
                .deletedByUserId(domain.getDeletedByUserId())
                .createdByRoleId(domain.getCreatedByUserRoleId())
                .updatedByRoleId(domain.getUpdatedByUserRoleId())
                .deletedByRoleId(domain.getDeletedByUserRoleId())
                .build();
    }

    public Customer toDomain(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }
        return Customer.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .rfc(entity.getRfc())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .createdByUserId(entity.getCreatedByUserId())
                .updatedByUserId(entity.getUpdatedByUserId())
                .deletedByUserId(entity.getDeletedByUserId())
                .createdByUserRoleId(entity.getCreatedByRoleId())
                .updatedByUserRoleId(entity.getUpdatedByRoleId())
                .deletedByUserRoleId(entity.getDeletedByRoleId())
                .build();
    }
}
