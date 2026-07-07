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

    public SupplierResponse toResponse(Supplier domain, String createdByName, String updatedByName) {
        return new SupplierResponse(
                domain.getId(),
                domain.getRfc(),
                domain.getBusinessName(),
                domain.getTaxRegimen(),
                domain.getContactEmail(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                createdByName,
                updatedByName
        );
    }
}
