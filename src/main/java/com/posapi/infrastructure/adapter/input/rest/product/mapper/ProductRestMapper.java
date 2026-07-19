package com.posapi.infrastructure.adapter.input.rest.product.mapper;

import com.posapi.domain.model.product.Product;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductRequest;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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
                .taxId(request.taxId())
                .supplierId(request.supplierId())
                .build();
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        // CORREGIDO: Pasar todos los campos requeridos por el constructor de ProductResponse
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getSalePrice(),
                product.getPurchasePrice(),
                product.getCurrentStock(),
                product.getCategoryId(), // Añadido
                product.getTaxId(),
                product.getSupplierId(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getDeletedAt(), // Añadido
                product.getCreatedByUserId(), // Añadido
                product.getUpdatedByUserId(), // Añadido
                product.getDeletedByUserId(), // Añadido
                product.getCreatedByUserRoleId(), // Añadido
                product.getUpdatedByUserRoleId(), // Añadido
                product.getDeletedByUserRoleId(), // Añadido
                null, // createdByName (se llenará en el servicio)
                null, // updatedByName (se llenará en el servicio)
                null  // deletedByName (se llenará en el servicio)
        );
    }
}
