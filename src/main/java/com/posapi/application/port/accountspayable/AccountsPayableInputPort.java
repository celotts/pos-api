package com.posapi.application.port.accountspayable;

// Importaciones necesarias
import com.posapi.infrastructure.adapter.input.rest.accountspayable.dto.AccountsPayableRequest; // DTO de entrada
import com.posapi.infrastructure.adapter.input.rest.accountspayable.dto.AccountsPayableResponse; // DTO de salida
import com.posapi.shared.dto.PageResponse; // Para respuestas paginadas
import org.springframework.data.domain.Pageable; // Para paginación

import java.math.BigDecimal; // Para montos monetarios
import java.time.LocalDate; // Para fechas
import java.util.Optional; // Para resultados que pueden no existir
import java.util.UUID; // Para IDs

public interface AccountsPayableInputPort {

    /**
     * Crea una nueva cuenta por pagar.
     * @param request DTO con los datos de la cuenta por pagar a crear.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un DTO de respuesta con la cuenta por pagar creada.
     */
    AccountsPayableResponse createAccountsPayable(AccountsPayableRequest request, UUID currentUserId);

    /**
     * Obtiene una cuenta por pagar por su ID.
     * @param id ID único de la cuenta por pagar.
     * @return Un Optional que contiene el DTO de respuesta si la cuenta existe.
     */
    Optional<AccountsPayableResponse> getAccountsPayableById(UUID id);

    /**
     * Obtiene una lista paginada de todas las cuentas por pagar.
     * @param pageable Objeto Pageable con los parámetros de paginación.
     * @return Una respuesta paginada de DTOs de cuentas por pagar.
     */
    PageResponse<AccountsPayableResponse> getAllAccountsPayable(Pageable pageable);

    /**
     * Obtiene una lista paginada de cuentas por pagar filtradas por proveedor.
     * @param supplierId ID del proveedor.
     * @param pageable Objeto Pageable con los parámetros de paginación.
     * @return Una respuesta paginada de DTOs de cuentas por pagar para el proveedor.
     */
    PageResponse<AccountsPayableResponse> getAccountsPayableBySupplier(UUID supplierId, Pageable pageable);

    /**
     * Actualiza una cuenta por pagar existente.
     * @param id ID de la cuenta por pagar a actualizar.
     * @param request DTO con los datos actualizados.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un DTO de respuesta con la cuenta por pagar actualizada.
     */
    AccountsPayableResponse updateAccountsPayable(UUID id, AccountsPayableRequest request, UUID currentUserId);

    /**
     * Registra un pago parcial o total en una cuenta por pagar.
     * @param id ID de la cuenta por pagar.
     * @param amountPaid Cantidad pagada en esta transacción.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un DTO de respuesta con la cuenta por pagar actualizada.
     */
    AccountsPayableResponse markAsPaid(UUID id, BigDecimal amountPaid, UUID currentUserId);

    /**
     * Elimina lógicamente una cuenta por pagar.
     * @param id ID de la cuenta por pagar a eliminar.
     * @param currentUserId ID del usuario que realiza la operación.
     */
    void deleteAccountsPayable(UUID id, UUID currentUserId);

    /**
     * Obtiene cuentas por pagar vencidas.
     * @param asOfDate Fecha de referencia para considerar "vencido".
     * @param pageable Objeto Pageable con los parámetros de paginación.
     * @return Una respuesta paginada de DTOs de cuentas por pagar vencidas.
     */
    PageResponse<AccountsPayableResponse> getOverdueAccountsPayable(LocalDate asOfDate, Pageable pageable);
}
