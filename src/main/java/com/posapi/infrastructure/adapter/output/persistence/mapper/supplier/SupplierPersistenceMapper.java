package com.posapi.infrastructure.adapter.output.persistence.mapper.supplier;

import com.posapi.domain.model.supplier.Supplier;
import com.posapi.infrastructure.adapter.output.persistence.entity.supplier.SupplierEntity;
import org.springframework.stereotype.Component;

@Component
public class SupplierPersistenceMapper {

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
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .deletedBy(entity.getDeletedBy())
                .build();
    }

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
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .deletedBy(domain.getDeletedBy())
                .build();
    }
}
