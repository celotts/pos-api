package com.posapi.infrastructure.adapter.output.persistence.mapper.sale;

import com.posapi.domain.model.sale.Sale;
import com.posapi.infrastructure.adapter.output.persistence.entity.sale.SaleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SalePersistenceMapper {

    public SaleEntity toEntity(Sale domain) {
        if (domain == null) {
            return null;
        }
        return SaleEntity.builder()
                .id(domain.getId())
                .customerId(domain.getCustomerId())
                .saleDate(domain.getSaleDate())
                .totalAmount(domain.getTotalAmount())
                .totalTaxAmount(domain.getTotalTaxAmount())
                .discountAmount(domain.getDiscountAmount())
                .status(domain.getStatus())
                .paymentStatus(domain.getPaymentStatus())
                .posTerminalId(domain.getPosTerminalId())
                .shiftId(domain.getShiftId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .deletedAt(domain.getDeletedAt())
                .createdByUserId(domain.getCreatedByUserId())
                .updatedByUserId(domain.getUpdatedByUserId())
                .deletedByUserId(domain.getDeletedByUserId())
                .createdByRoleId(domain.getCreatedByUserRoleId())
                .updatedByRoleId(domain.getUpdatedByUserRoleId())
                .deletedByRoleId(domain.getDeletedByUserRoleId())
                .build();
    }

    public Sale toDomain(SaleEntity entity) {
        if (entity == null) {
            return null;
        }
        return Sale.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .saleDate(entity.getSaleDate())
                .totalAmount(entity.getTotalAmount())
                .totalTaxAmount(entity.getTotalTaxAmount())
                .discountAmount(entity.getDiscountAmount())
                .status(entity.getStatus())
                .paymentStatus(entity.getPaymentStatus())
                .posTerminalId(entity.getPosTerminalId())
                .shiftId(entity.getShiftId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .createdByUserId(entity.getCreatedByUserId())
                .updatedByUserId(entity.getUpdatedByUserId())
                .deletedByUserId(entity.getDeletedByUserId())
                .createdByUserRoleId(entity.getCreatedByRoleId())
                .updatedByUserRoleId(entity.getUpdatedByRoleId())
                .deletedByUserRoleId(entity.getDeletedByRoleId())
                .build();
    }

    public List<Sale> toDomainList(List<SaleEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}
