package com.posapi.infrastructure.adapter.input.rest.sale.mapper;

import com.posapi.domain.model.sale.Sale;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleRequest;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SaleRestMapper {

    Sale toDomain(SaleRequest request);

    // Mapeo de Sale a SaleResponse
    // Asumimos que SaleResponse tiene un constructor o setters para estos campos
    @Mapping(target = "customerName", ignore = true) // Se llenará en el servicio
    @Mapping(target = "createdByName", ignore = true) // Se llenará en el servicio
    @Mapping(target = "updatedByName", ignore = true) // Se llenará en el servicio
    @Mapping(target = "items", ignore = true) // Se llenará en el servicio
    SaleResponse toResponse(Sale sale);

    List<SaleResponse> toResponseList(List<Sale> sales);
}
