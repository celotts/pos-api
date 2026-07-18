package com.posapi.application.port.cashAccount;

import com.posapi.infrastructure.adapter.input.rest.cashAccount.dto.CashAccountRequest;
import com.posapi.infrastructure.adapter.input.rest.cashAccount.dto.CashAccountResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CashAccountManagementPort {

    /**
     * Crea una nueva cuenta de caja.
     * @param request DTO con los datos de la cuenta a crear.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un DTO de respuesta con la cuenta creada.
     */
    CashAccountResponse createCashAccount(CashAccountRequest request, UUID currentUserId);

    /**
     * Obtiene una cuenta de caja por su ID.
     * @param id ID único de la cuenta.
     * @return Un Optional que contiene el DTO de respuesta si la cuenta existe.
     */
    Optional<CashAccountResponse> getCashAccountById(UUID id);

    /**
     * Obtiene una lista de todas las cuentas de caja.
     * @return Una lista de DTOs de respuesta de cuentas de caja.
     */
    List<CashAccountResponse> getAllCashAccounts();

    /**
     * Actualiza una cuenta de caja existente.
     * @param id ID de la cuenta a actualizar.
     * @param request DTO con los datos actualizados de la cuenta.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un Optional que contiene el DTO de respuesta con la cuenta actualizada.
     */
    Optional<CashAccountResponse> updateCashAccount(UUID id, CashAccountRequest request, UUID currentUserId);

    /**
     * Elimina lógicamente una cuenta de caja.
     * @param id ID de la cuenta a eliminar.
     * @param currentUserId ID del usuario que realiza la operación.
     */
    void deleteCashAccount(UUID id, UUID currentUserId);
}
