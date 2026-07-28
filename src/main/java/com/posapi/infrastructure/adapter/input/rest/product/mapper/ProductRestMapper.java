package com.posapi.infrastructure.adapter.input.rest.product.mapper;

import com.posapi.domain.model.product.Product;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductRequest;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductRestMapper {

    Product toDomain(ProductRequest request);

    ProductResponse toResponse(Product product, @Context Map<UUID, String> userNames);
}
