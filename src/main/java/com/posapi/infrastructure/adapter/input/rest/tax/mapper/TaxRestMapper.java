package com.posapi.infrastructure.adapter.input.rest.tax.mapper;

import com.posapi.domain.model.tax.Tax;
import com.posapi.infrastructure.adapter.input.rest.tax.dto.TaxRequest;
import com.posapi.infrastructure.adapter.input.rest.tax.dto.TaxResponse;
import org.springframework.stereotype.Component;


@Component
public class TaxRestMapper {

    public Tax toDomain(TaxRequest request) {
        if (request == null) {
            return null;
        }
        return Tax.builder()
                .name(request.name())
                .percentage(request.percentage())
                .taxType(request.taxType())
                .build();
    }

    // Método para mapear de Tax (dominio) a TaxResponse (DTO)
    public TaxResponse toResponse(Tax tax, String createdByName, String updatedByName, String deletedByName) {
        if (tax == null) {
            return null;
        }
        // CORREGIDO: Usar el método estático fromDomain del record TaxResponse
        return TaxResponse.fromDomain(tax, createdByName, updatedByName, deletedByName);
    }
}
