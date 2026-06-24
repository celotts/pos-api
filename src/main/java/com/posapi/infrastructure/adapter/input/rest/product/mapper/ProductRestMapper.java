package com.posapi.infrastructure.adapter.input.rest.product.mapper;

import com.posapi.domain.model.product.Product;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductRequest;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductRestMapper {

    public Product toDomain(ProductRequest request) {
        if (request == null) return null;

        return Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .purchasePrice(request.getPurchasePrice())
                .salePrice(request.getSalePrice())
                .currentStock(request.getCurrentStock() != null ? request.getCurrentStock().intValue() : 0)
                .taxId(request.getTaxId())
                .supplierId(request.getSupplierId())
                .build();
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) return null;

        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .purchasePrice(product.getPurchasePrice())
                .salePrice(product.getSalePrice())
                .currentStock(BigDecimal.valueOf(product.getCurrentStock()))
                .taxId(product.getTaxId())
                .supplierId(product.getSupplierId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .deletedAt(product.getDeletedAt())
                .createdByUserId(product.getCreatedByUserId())
                .updatedByUserId(product.getUpdatedByUserId())
                .deletedByUserId(product.getDeletedByUserId())
                .build();
    }
}