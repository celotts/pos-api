package com.posapi.infrastructure.adapter.output.persistence.mapper.supplier;

import com.posapi.domain.model.supplier.Supplier;
import com.posapi.infrastructure.adapter.output.persistence.entity.supplier.SupplierEntity;
import org.springframework.stereotype.Component;

@Component
public class SupplierPersistenceMapper {

    public SupplierEntity toEntity(Supplier domain) {
        if (domain == null) {
            return null;
        }
        return SupplierEntity.builder()
                .id(domain.getId())
                .rfc(domain.getRfc())
                .businessName(domain.getBusinessName())
                .taxRegimen(domain.getTaxRegimen())
                .contactEmail(domain.getContactEmail())
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

    public Supplier toDomain(SupplierEntity entity) {
        if (entity == null) {
            return null;
        }
        return Supplier.builder()
                .id(entity.getId())
                .rfc(entity.getRfc())
                .businessName(entity.getBusinessName())
                .taxRegimen(entity.getTaxRegimen())
                .contactEmail(entity.getContactEmail())
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
