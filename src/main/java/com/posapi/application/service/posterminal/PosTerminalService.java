package com.posapi.application.service.posterminal;

import com.posapi.application.port.posterminal.PosTerminalManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.posterminal.PosTerminal;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.PosTerminalRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.posterminal.dto.PosTerminalRequest;
import com.posapi.infrastructure.adapter.input.rest.posterminal.dto.PosTerminalResponse;
import com.posapi.infrastructure.security.SecurityContextHelper;
import com.posapi.shared.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PosTerminalService implements PosTerminalManagementPort {

    private final PosTerminalRepository posTerminalRepository;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;

    @Override
    @Transactional
    public PosTerminalResponse createPosTerminal(PosTerminalRequest request, UUID currentUserId) {
        if (posTerminalRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("POS Terminal with name '" + request.name() + "' already exists.");
        }

        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        PosTerminal newPosTerminal = PosTerminal.createNew(
                request.name(),
                request.location(),
                currentUserId,
                currentUserRoleId
        );

        PosTerminal savedPosTerminal = posTerminalRepository.save(newPosTerminal);
        return mapToPosTerminalResponse(savedPosTerminal);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PosTerminalResponse> getPosTerminalById(UUID id) {
        return posTerminalRepository.findById(id).map(this::mapToPosTerminalResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PosTerminalResponse> getAllPosTerminals(Pageable pageable) {
        Page<PosTerminal> posTerminalsPage = posTerminalRepository.findAll(pageable);
        List<PosTerminalResponse> content = posTerminalsPage.getContent().stream()
                .map(this::mapToPosTerminalResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                content,
                posTerminalsPage.getNumber(),
                posTerminalsPage.getSize(),
                posTerminalsPage.getTotalElements(),
                posTerminalsPage.getTotalPages(),
                posTerminalsPage.isLast()
        );
    }

    @Override
    @Transactional
    public Optional<PosTerminalResponse> updatePosTerminal(UUID id, PosTerminalRequest request, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        return posTerminalRepository.findById(id).map(existingPosTerminal -> {
            if (request.name() != null && !request.name().equals(existingPosTerminal.getName())) {
                if (posTerminalRepository.existsByName(request.name())) {
                    throw new DuplicateResourceException("POS Terminal with name '" + request.name() + "' already exists.");
                }
            }
            existingPosTerminal.updateDetails(
                    request.name(),
                    request.location(),
                    request.isActive(),
                    currentUserId,
                    currentUserRoleId
            );
            PosTerminal updatedPosTerminal = posTerminalRepository.save(existingPosTerminal);
            return mapToPosTerminalResponse(updatedPosTerminal);
        });
    }

    @Override
    @Transactional
    public void deletePosTerminal(UUID id, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        posTerminalRepository.findById(id).ifPresent(existingPosTerminal -> {
            existingPosTerminal.markAsDeleted(currentUserId, currentUserRoleId);
            posTerminalRepository.save(existingPosTerminal);
            log.info("POS Terminal with id {} marked as deleted by user {}", id, currentUserId);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PosTerminalResponse> getPosTerminalByName(String name) {
        return posTerminalRepository.findByName(name).map(this::mapToPosTerminalResponse);
    }

    private PosTerminalResponse mapToPosTerminalResponse(PosTerminal posTerminal) {
        Set<UUID> userIds = Stream.of(
                posTerminal.getCreatedByUserId(),
                posTerminal.getUpdatedByUserId(),
                posTerminal.getDeletedByUserId()
        ).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<UUID, String> userNames = fetchUserNames(userIds);

        String createdByName = userNames.getOrDefault(posTerminal.getCreatedByUserId(), null);
        String updatedByName = userNames.getOrDefault(posTerminal.getUpdatedByUserId(), null);
        String deletedByName = userNames.getOrDefault(posTerminal.getDeletedByUserId(), null);

        return PosTerminalResponse.fromDomain(posTerminal, createdByName, updatedByName, deletedByName);
    }

    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }
}
