package com.posapi.application.port.tax;

import com.posapi.infrastructure.adapter.input.rest.tax.dto.TaxRequest;
import com.posapi.infrastructure.adapter.input.rest.tax.dto.TaxResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaxManagementPort {

    /**
     * Crea un nuevo impuesto.
     * @param request DTO con los datos del impuesto a crear.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un DTO de respuesta con el impuesto creado.
     */
    TaxResponse createTax(TaxRequest request, UUID currentUserId);

    /**
     * Obtiene un impuesto por su ID.
     * @param id ID único del impuesto.
     * @return Un Optional que contiene el DTO de respuesta si el impuesto existe.
     */
    Optional<TaxResponse> getTaxById(UUID id);

    /**
     * Obtiene una lista de todos los impuestos.
     * @return Una lista de DTOs de respuesta de impuestos.
     */
    List<TaxResponse> getAllTaxes();

    /**
     * Actualiza un impuesto existente.
     * @param id ID del impuesto a actualizar.
     * @param request DTO con los datos actualizados del impuesto.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un Optional que contiene el DTO de respuesta con el impuesto actualizado.
     */
    Optional<TaxResponse> updateTax(UUID id, TaxRequest request, UUID currentUserId);

    /**
     * Elimina lógicamente un impuesto.
     * @param id ID del impuesto a eliminar.
     * @param currentUserId ID del usuario que realiza la operación.
     */
    void deleteTax(UUID id, UUID currentUserId);
}
