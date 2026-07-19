package com.posapi.application.port.shift;

import com.posapi.infrastructure.adapter.input.rest.shift.dto.ShiftRequest;
import com.posapi.infrastructure.adapter.input.rest.shift.dto.ShiftResponse;
import com.posapi.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShiftManagementPort {

    /**
     * Crea un nuevo turno.
     * @param request DTO con los datos del turno a crear.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un DTO de respuesta con el turno creado.
     */
    ShiftResponse createShift(ShiftRequest request, UUID currentUserId);

    /**
     * Obtiene un turno por su ID.
     * @param id ID único del turno.
     * @return Un Optional que contiene el DTO de respuesta si el turno existe.
     */
    Optional<ShiftResponse> getShiftById(UUID id);

    /**
     * Obtiene una lista paginada de todos los turnos.
     * @param pageable Objeto Pageable con los parámetros de paginación.
     * @return Una respuesta paginada de DTOs de turnos.
     */
    PageResponse<ShiftResponse> getAllShifts(Pageable pageable);

    /**
     * Actualiza un turno existente.
     * @param id ID del turno a actualizar.
     * @param request DTO con los datos actualizados del turno.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un Optional que contiene el DTO de respuesta con el turno actualizado.
     */
    Optional<ShiftResponse> updateShift(UUID id, ShiftRequest request, UUID currentUserId);

    /**
     * Cierra un turno.
     * @param id ID del turno a cerrar.
     * @param endingCash Cantidad de efectivo al finalizar el turno.
     * @param currentUserId ID del usuario que realiza la operación.
     * @return Un Optional que contiene el DTO de respuesta con el turno cerrado.
     */
    Optional<ShiftResponse> closeShift(UUID id, BigDecimal endingCash, UUID currentUserId);

    /**
     * Cancela un turno.
     * @param id ID del turno a cancelar.
     * @param currentUserId ID del usuario que realiza la operación.
     */
    void cancelShift(UUID id, UUID currentUserId);

    /**
     * Elimina lógicamente un turno.
     * @param id ID del turno a eliminar.
     * @param currentUserId ID del usuario que realiza la operación.
     */
    void deleteShift(UUID id, UUID currentUserId);
}
