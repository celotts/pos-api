package com.posapi.application.port.supplier;

import com.posapi.infrastructure.adapter.input.rest.supplier.dto.SupplierRequest;
import com.posapi.infrastructure.adapter.input.rest.supplier.dto.SupplierResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupplierManagementPort {

    /**
     * Crea un nuevo proveedor.
     * @param request DTO con los datos del proveedor a crear.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un DTO de respuesta con el proveedor creado.
     */
    SupplierResponse createSupplier(SupplierRequest request, UUID currentUserId);

    /**
     * Obtiene un proveedor por su ID.
     * @param id ID único del proveedor.
     * @return Un Optional que contiene el DTO de respuesta si el proveedor existe.
     */
    Optional<SupplierResponse> getSupplierById(UUID id);

    /**
     * Obtiene una lista de todos los proveedores.
     * @return Una lista de DTOs de respuesta de proveedores.
     */
    List<SupplierResponse> getAllSuppliers();

    /**
     * Actualiza un proveedor existente.
     * @param id ID del proveedor a actualizar.
     * @param request DTO con los datos actualizados del proveedor.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un Optional que contiene el DTO de respuesta con el proveedor actualizado.
     */
    Optional<SupplierResponse> updateSupplier(UUID id, SupplierRequest request, UUID currentUserId);

    /**
     * Elimina lógicamente un proveedor.
     * @param id ID del proveedor a eliminar.
     * @param currentUserId ID del usuario que realiza la operación.
     */
    void deleteSupplier(UUID id, UUID currentUserId);
}
