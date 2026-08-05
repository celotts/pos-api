package com.posapi.infrastructure.adapter.input.rest.sale.mapper;

import com.posapi.domain.model.sale.Sale;
import com.posapi.infrastructure.adapter.input.rest.mapper.AuditingMapperConfig;
import com.posapi.infrastructure.adapter.input.rest.mapper.IgnoreAuditingOnCreate;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleRequest;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", config = AuditingMapperConfig.class)
public interface SaleRestMapper {

    @IgnoreAuditingOnCreate
    @Mapping(target = "saleDate", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "totalTaxAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    Sale toDomain(SaleRequest request);

    SaleResponse toResponse(Sale sale);

    List<SaleResponse> toResponseList(List<Sale> sales);
}
