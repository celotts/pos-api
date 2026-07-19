package com.posapi.feature.accountspayable.infrastructure.adapter.output.persistence.mapper;

import com.posapi.domain.model.accountspayable.AccountsPayable;
import com.posapi.feature.accountspayable.infrastructure.adapter.output.persistence.entity.AccountsPayableEntity;
import org.springframework.stereotype.Component;

@Component
public class AccountsPayablePersistenceMapper {

    public AccountsPayableEntity toEntity(AccountsPayable domain) {
        if (domain == null) {
            return null;
        }
        return AccountsPayableEntity.builder()
                .id(domain.getId())
                .purchaseId(domain.getPurchaseId())
                .supplierId(domain.getSupplierId())
                .originalAmount(domain.getOriginalAmount())
                .outstandingAmount(domain.getOutstandingAmount())
                .dueDate(domain.getDueDate())
                .status(toEntityStatus(domain.getStatus()))
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .deletedAt(domain.getDeletedAt())
                .createdByUserId(domain.getCreatedByUserId())
                .updatedByUserId(domain.getUpdatedByUserId())
                .deletedByUserId(domain.getDeletedByUserId())
                .createdByRoleId(domain.getCreatedByUserRoleId()) // CORREGIDO
                .updatedByRoleId(domain.getUpdatedByUserRoleId()) // CORREGIDO
                .deletedByRoleId(domain.getDeletedByUserRoleId()) // CORREGIDO
                .build();
    }

    public AccountsPayable toDomain(AccountsPayableEntity entity) {
        if (entity == null) {
            return null;
        }
        return AccountsPayable.builder()
                .id(entity.getId())
                .purchaseId(entity.getPurchaseId())
                .supplierId(entity.getSupplierId())
                .originalAmount(entity.getOriginalAmount())
                .outstandingAmount(entity.getOutstandingAmount())
                .dueDate(entity.getDueDate())
                .status(toDomainStatus(entity.getStatus()))
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

    private AccountsPayableEntity.ArApStatus toEntityStatus(AccountsPayable.ArApStatus domainStatus) {
        if (domainStatus == null) return null;
        switch (domainStatus) {
            case OPEN: return AccountsPayableEntity.ArApStatus.OPEN;
            case CLOSED: return AccountsPayableEntity.ArApStatus.CLOSED;
            case OVERDUE: return AccountsPayableEntity.ArApStatus.OVERDUE;
            case PENDING: return AccountsPayableEntity.ArApStatus.PENDING;
            case PAID: return AccountsPayableEntity.ArApStatus.PAID;
            case CANCELED: return AccountsPayableEntity.ArApStatus.CANCELED;
            default: throw new IllegalArgumentException("Unknown domain ArApStatus: " + domainStatus);
        }
    }

    private AccountsPayable.ArApStatus toDomainStatus(AccountsPayableEntity.ArApStatus entityStatus) {
        if (entityStatus == null) return null;
        switch (entityStatus) {
            case OPEN: return AccountsPayable.ArApStatus.OPEN;
            case CLOSED: return AccountsPayable.ArApStatus.CLOSED;
            case OVERDUE: return AccountsPayable.ArApStatus.OVERDUE;
            case PENDING: return AccountsPayable.ArApStatus.PENDING;
            case PAID: return AccountsPayable.ArApStatus.PAID;
            case CANCELED: return AccountsPayable.ArApStatus.CANCELED;
            default: throw new IllegalArgumentException("Unknown entity ArApStatus: " + entityStatus);
        }
    }
}
