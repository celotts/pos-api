package com.posapi.application.port.posterminal;

import com.posapi.infrastructure.adapter.input.rest.posterminal.dto.PosTerminalRequest; // CORREGIDO
import com.posapi.infrastructure.adapter.input.rest.posterminal.dto.PosTerminalResponse; // CORREGIDO
import com.posapi.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PosTerminalManagementPort {

    PosTerminalResponse createPosTerminal(PosTerminalRequest request, UUID currentUserId);

    Optional<PosTerminalResponse> getPosTerminalById(UUID id);

    PageResponse<PosTerminalResponse> getAllPosTerminals(Pageable pageable);

    Optional<PosTerminalResponse> updatePosTerminal(UUID id, PosTerminalRequest request, UUID currentUserId);

    void deletePosTerminal(UUID id, UUID currentUserId);

    Optional<PosTerminalResponse> getPosTerminalByName(String name);
}
