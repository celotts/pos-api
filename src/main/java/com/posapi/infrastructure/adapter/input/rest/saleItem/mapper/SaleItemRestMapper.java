package com.posapi.infrastructure.adapter.input.rest.saleItem.mapper;

import com.posapi.domain.model.product.Product;
import com.posapi.domain.model.sale.SaleItem;
import com.posapi.infrastructure.adapter.input.rest.saleItem.dto.SaleItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SaleItemRestMapper {

    // Make toResponse a default method to handle null saleItem
    default SaleItemResponse toResponse(SaleItem saleItem, Product product, Map<UUID, String> userNames) {
        if (saleItem == null) {
            return null;
        }

        SaleItemResponse.SaleItemResponseBuilder builder = SaleItemResponse.builder(); // Corrected this line

        // Map properties from saleItem
        builder.id(saleItem.getId());
        builder.saleId(saleItem.getSaleId());
        builder.productId(saleItem.getProductId());
        builder.quantity(saleItem.getQuantity());
        builder.unitPrice(saleItem.getUnitPrice());
        builder.subtotal(saleItem.getSubtotal());
        builder.createdAt(saleItem.getCreatedAt());
        builder.updatedAt(saleItem.getUpdatedAt());
        builder.deletedAt(saleItem.getDeletedAt());
        builder.createdByUserId(saleItem.getCreatedByUserId());
        builder.updatedByUserId(saleItem.getUpdatedByUserId());
        builder.deletedByUserId(saleItem.getDeletedByUserId());
        builder.createdByUserRoleId(saleItem.getCreatedByUserRoleId());
        builder.updatedByUserRoleId(saleItem.getUpdatedByUserRoleId());
        builder.deletedByUserRoleId(saleItem.getDeletedByUserRoleId());

        // Map properties from product if not null
        if (product != null) {
            builder.productName(product.getName());
            builder.productSku(product.getSku());
        }

        // Map user names if userNames map is provided
        if (userNames != null) {
            builder.createdByName(userNames.get(saleItem.getCreatedByUserId()));
            builder.updatedByName(userNames.get(saleItem.getUpdatedByUserId()));
            builder.deletedByName(userNames.get(saleItem.getDeletedByUserId()));
        }

        return builder.build();
    }

    default List<SaleItemResponse> toResponseList(List<SaleItem> saleItems, Map<UUID, Product> productMap, Map<UUID, String> userNames) {
        if (saleItems == null) {
            return Collections.emptyList();
        }
        return saleItems.stream()
                .filter(Objects::nonNull) // Filter out any null items in the list
                .map(saleItem -> toResponse(saleItem, productMap.get(saleItem.getProductId()), userNames))
                .collect(Collectors.toList());
    }
}
