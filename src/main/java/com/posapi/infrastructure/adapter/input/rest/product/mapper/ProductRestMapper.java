package com.posapi.infrastructure.adapter.input.rest.product.mapper;

import com.posapi.domain.model.product.Product;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductRequest;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductResponse;

import java.util.Map;
import java.util.UUID;

public interface ProductRestMapper {

    Product toDomain(ProductRequest request);

    ProductResponse toResponse(Product product, Map<UUID, String> userNames);
}
