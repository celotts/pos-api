package com.posapi.application.port.customer;


import com.posapi.infrastructure.adapter.input.rest.customer.dto.CustomerRequest;
import com.posapi.infrastructure.adapter.input.rest.customer.dto.CustomerResponse;
import com.posapi.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CustomerInputPort {

    /**
     * Crea un nuevo cliente.
     * @param request DTO con los datos del cliente a crear.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un DTO de respuesta con el cliente creado.
     */
    CustomerResponse createCustomer(CustomerRequest request, UUID currentUserId);

    /**
     * Obtiene un cliente por su ID.
     * @param id ID único del cliente.
     * @return Un Optional que contiene el DTO de respuesta si el cliente existe.
     */
    Optional<CustomerResponse> getCustomerById(UUID id);

    /**
     * Obtiene una lista paginada de todos los clientes.
     * @param pageable Objeto Pageable con los parámetros de paginación.
     * @return Una respuesta paginada de DTOs de clientes.
     */
    PageResponse<CustomerResponse> getAllCustomers(Pageable pageable);

    /**
     * Actualiza un cliente existente.
     * @param id ID del cliente a actualizar.
     * @param request DTO con los datos actualizados.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un Optional que contiene el DTO de respuesta del cliente actualizado si existe.
     */
    Optional<CustomerResponse> updateCustomer(UUID id, CustomerRequest request, UUID currentUserId);

    /**
     * Elimina lógicamente un cliente.
     * @param id ID del cliente a eliminar.
     * @param currentUserId ID del usuario que realiza la operación.
     */
    void deleteCustomer(UUID id, UUID currentUserId);

    /**
     * Obtiene un cliente por su RFC.
     * @param rfc RFC del cliente.
     * @return Un Optional que contiene el DTO de respuesta si el cliente existe.
     */
    Optional<CustomerResponse> getCustomerByRfc(String rfc);
}
