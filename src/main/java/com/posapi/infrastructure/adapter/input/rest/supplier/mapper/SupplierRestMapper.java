package com.posapi.infrastructure.adapter.input.rest.supplier.mapper;

import com.posapi.domain.model.supplier.Supplier;
import com.posapi.infrastructure.adapter.input.rest.supplier.dto.SupplierRequest;
import com.posapi.infrastructure.adapter.input.rest.supplier.dto.SupplierResponse;
import org.springframework.stereotype.Component;

@Component
public class SupplierRestMapper {

    public Supplier toDomain(SupplierRequest dto) {
        return Supplier.builder()
                .rfc(dto.rfc())
                .businessName(dto.businessName())
                .taxRegimen(dto.taxRegimen())
                .contactEmail(dto.contactEmail())
                .build();
    }

    // CORREGIDO: Añadido deletedByName a la firma del método
    public SupplierResponse toResponse(Supplier domain, String createdByName, String updatedByName, String deletedByName) {
        return new SupplierResponse(
                domain.getId(),
                domain.getRfc(),
                domain.getBusinessName(),
                domain.getTaxRegimen(),
                domain.getContactEmail(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getDeletedAt(), // Añadido
                domain.getCreatedByUserId(), // Añadido
                domain.getUpdatedByUserId(), // Añadido
                domain.getDeletedByUserId(), // Añadido
                domain.getCreatedByUserRoleId(), // Añadido
                domain.getUpdatedByUserRoleId(), // Añadido
                domain.getDeletedByUserRoleId(), // Añadido
                createdByName,
                updatedByName,
                deletedByName // Añadido
        );
    }
}
