package com.posapi.infrastructure.adapter.input.rest.product.mapper;

import com.posapi.domain.model.product.Product;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductRequest;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant; // AÑADIDO
import java.util.UUID; // AÑADIDO

@Component
public class ProductRestMapper {

    public Product toDomain(@Valid ProductRequest request) {
        if (request == null) {
            return null;
        }

        return Product.builder()
                .sku(request.sku())
                .name(request.name())
                .description(request.description())
                .purchasePrice(request.purchasePrice())
                .salePrice(request.salePrice())
                .currentStock(request.currentStock() != null ? request.currentStock() : BigDecimal.ZERO)
                .categoryId(request.categoryId())
                .taxId(request.taxId())
                .supplierId(request.supplierId())
                .build();
    }

    // CORREGIDO: Método toResponse para aceptar nombres de auditoría
    public ProductResponse toResponse(Product product, String createdByName, String updatedByName, String deletedByName) {
        if (product == null) {
            return null;
        }

        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getSalePrice(),
                product.getPurchasePrice(),
                product.getCurrentStock(),
                product.getCategoryId(),
                product.getTaxId(),
                product.getSupplierId(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getDeletedAt(),
                product.getCreatedByUserId(),
                product.getUpdatedByUserId(),
                product.getDeletedByUserId(),
                product.getCreatedByUserRoleId(),
                product.getUpdatedByUserRoleId(),
                product.getDeletedByUserRoleId(),
                createdByName,
                updatedByName,
                deletedByName
        );
    }
}
