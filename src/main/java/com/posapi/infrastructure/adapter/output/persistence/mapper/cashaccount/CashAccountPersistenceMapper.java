package com.posapi.infrastructure.adapter.output.persistence.mapper.cashaccount;

import com.posapi.domain.model.cashaccount.CashAccount;
import com.posapi.infrastructure.adapter.output.persistence.entity.cashaccount.CashAccountEntity;
import org.springframework.stereotype.Component;

@Component
public class CashAccountPersistenceMapper {

    public CashAccountEntity toEntity(CashAccount domain) {
        if (domain == null) {
            return null;
        }
        return CashAccountEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .accountType(domain.getAccountType())
                .currentBalance(domain.getCurrentBalance())
                .currency(domain.getCurrency())
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

    public CashAccount toDomain(CashAccountEntity entity) {
        if (entity == null) {
            return null;
        }
        return CashAccount.builder()
                .id(entity.getId())
                .name(entity.getName())
                .accountType(entity.getAccountType())
                .currentBalance(entity.getCurrentBalance())
                .currency(entity.getCurrency())
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
}
