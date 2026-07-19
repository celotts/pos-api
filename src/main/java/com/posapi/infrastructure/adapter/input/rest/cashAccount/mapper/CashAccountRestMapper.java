package com.posapi.infrastructure.adapter.input.rest.cashAccount.mapper;

import com.posapi.domain.model.cashaccount.CashAccount;

import com.posapi.infrastructure.adapter.input.rest.cashAccount.dto.CashAccountRequest;
import com.posapi.infrastructure.adapter.input.rest.cashAccount.dto.CashAccountResponse;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class CashAccountRestMapper {

    /**
     * Convierte la petición externa (HTTP) al modelo de negocio puro (Dominio).
     */
    public CashAccount toDomain(CashAccountRequest request, UUID currentUserId, UUID currentUserRoleId) {
        if (request == null) return null;

        // 💡 Ajustado para cumplir exactamente con la firma de 6 parámetros de tu dominio
        return CashAccount.createNew(
                request.name(),
                request.accountType(), // CORREGIDO: Ya es un CashAccountType, no necesita valueOf ni toUpperCase
                request.initialBalance(), // CORREGIDO: Usar initialBalance del request
                request.currency(),
                currentUserId,
                currentUserRoleId
        );
    }

    /**
     * Convierte el modelo de negocio (Dominio) a la respuesta que verá el cliente (HTTP).
     */
    public CashAccountResponse toResponse(
            CashAccount cashAccount,
            String createdByName,
            String updatedByName,
            String deletedByName) {

        if (cashAccount == null) return null;

        return CashAccountResponse.fromDomain(
                cashAccount,
                createdByName,
                updatedByName,
                deletedByName
        );
    }
}
