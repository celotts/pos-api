package com.posapi.infrastructure.adapter.input.rest.product.mapper;

import com.posapi.domain.model.product.Product;
import com.posapi.infrastructure.adapter.input.rest.dto.product.ProductRequest;
import com.posapi.infrastructure.adapter.input.rest.dto.product.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductRestMapper {

    public Product toDomain(@Valid ProductRequest request) {
        if (request == null) return null;

        return Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .purchasePrice(request.getPurchasePrice())
                .salePrice(request.getSalePrice())
                .currentStock(request.getCurrentStock() != null ? request.getCurrentStock() : BigDecimal.ZERO)
                .taxId(request.getTaxId())
                .supplierId(request.getSupplierId())
                .build();
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) return null;

        return ProductResponse.fromDomain(product);
    }
}
