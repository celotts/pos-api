package com.posapi.application.port.posterminal;

import com.posapi.infrastructure.adapter.input.rest.posterminal.dto.PosTerminalRequest;
import com.posapi.infrastructure.adapter.input.rest.posterminal.dto.PosTerminalResponse;
import com.posapi.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PosTerminalManagementPort {

    /**
     * Crea una nueva terminal de punto de venta.
     * @param request DTO con los datos de la terminal a crear.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un DTO de respuesta con la terminal creada.
     */
    PosTerminalResponse createPosTerminal(PosTerminalRequest request, UUID currentUserId);

    /**
     * Obtiene una terminal de punto de venta por su ID.
     * @param id ID único de la terminal.
     * @return Un Optional que contiene el DTO de respuesta si la terminal existe.
     */
    Optional<PosTerminalResponse> getPosTerminalById(UUID id);

    /**
     * Obtiene una lista paginada de todas las terminales de punto de venta.
     * @param pageable Objeto Pageable con los parámetros de paginación.
     * @return Una respuesta paginada de DTOs de terminales.
     */
    PageResponse<PosTerminalResponse> getAllPosTerminals(Pageable pageable);

    /**
     * Actualiza una terminal de punto de venta existente.
     * @param id ID de la terminal a actualizar.
     * @param request DTO con los datos actualizados de la terminal.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un Optional que contiene el DTO de respuesta con la terminal actualizada.
     */
    Optional<PosTerminalResponse> updatePosTerminal(UUID id, PosTerminalRequest request, UUID currentUserId);

    /**
     * Elimina lógicamente una terminal de punto de venta.
     * @param id ID de la terminal a eliminar.
     * @param currentUserId ID del usuario que realiza la operación.
     */
    void deletePosTerminal(UUID id, UUID currentUserId);

    /**
     * Obtiene una terminal de punto de venta por su nombre.
     * @param name Nombre de la terminal.
     * @return Un Optional que contiene el DTO de respuesta si la terminal existe.
     */
    Optional<PosTerminalResponse> getPosTerminalByName(String name);
}
