package com.posapi.application.service.shift;

import com.posapi.application.port.shift.ShiftManagementPort;
import com.posapi.domain.model.shift.Shift;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.ShiftRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.shift.dto.ShiftRequest;
import com.posapi.infrastructure.adapter.input.rest.shift.dto.ShiftResponse;
import com.posapi.infrastructure.adapter.input.rest.shift.mapper.ShiftRestMapper;
import com.posapi.infrastructure.security.SecurityContextHelper;
import com.posapi.shared.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
public class ShiftService implements ShiftManagementPort {

    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;
    private final ShiftRestMapper shiftRestMapper;

    @Override
    @Transactional
    public ShiftResponse createShift(ShiftRequest request, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        Shift newShift = Shift.createNew(
                request.userId(),
                request.posTerminalId(),
                request.startingCash(),
                currentUserId,
                currentUserRoleId
        );

        Shift savedShift = shiftRepository.save(newShift);
        return mapToShiftResponse(savedShift);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShiftResponse> getShiftById(UUID id) {
        return shiftRepository.findById(id).map(this::mapToShiftResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ShiftResponse> getAllShifts(Pageable pageable) {
        Page<Shift> shiftsPage = shiftRepository.findAll(pageable);
        List<ShiftResponse> content = shiftsPage.getContent().stream()
                .map(this::mapToShiftResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                content,
                shiftsPage.getNumber(),
                shiftsPage.getSize(),
                shiftsPage.getTotalElements(),
                shiftsPage.getTotalPages(),
                shiftsPage.isLast()
        );
    }

    @Override
    @Transactional
    public Optional<ShiftResponse> updateShift(UUID id, ShiftRequest request, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        return shiftRepository.findById(id).map(existingShift -> {

            existingShift.setUpdatedAt(java.time.Instant.now());
            existingShift.setUpdatedByUserId(currentUserId);
            existingShift.setUpdatedByUserRoleId(currentUserRoleId);

            Shift updatedShift = shiftRepository.save(existingShift);
            return mapToShiftResponse(updatedShift);
        });
    }

    @Override
    @Transactional
    public Optional<ShiftResponse> closeShift(UUID id, BigDecimal endingCash, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        return shiftRepository.findById(id).map(existingShift -> {
            existingShift.closeShift(endingCash, currentUserId, currentUserRoleId);
            Shift closedShift = shiftRepository.save(existingShift);
            return mapToShiftResponse(closedShift);
        });
    }

    @Override
    @Transactional
    public void cancelShift(UUID id, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        shiftRepository.findById(id).ifPresent(existingShift -> {
            existingShift.cancelShift(currentUserId, currentUserRoleId);
            shiftRepository.save(existingShift);
            log.info("Shift with id {} cancelled by user {}", id, currentUserId);
        });
    }

    @Override
    @Transactional
    public void deleteShift(UUID id, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        shiftRepository.findById(id).ifPresent(existingShift -> {
            existingShift.markAsDeleted(currentUserId, currentUserRoleId);
            shiftRepository.save(existingShift);
            log.info("Shift with id {} marked as deleted by user {}", id, currentUserId);
        });
    }

    private ShiftResponse mapToShiftResponse(Shift shift) {
        Set<UUID> userIds = Stream.of(
                shift.getCreatedByUserId(),
                shift.getUpdatedByUserId(),
                shift.getDeletedByUserId()
        ).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<UUID, String> userNames = fetchUserNames(userIds);

        String createdByName = userNames.getOrDefault(shift.getCreatedByUserId(), null);
        String updatedByName = userNames.getOrDefault(shift.getUpdatedByUserId(), null);
        String deletedByName = userNames.getOrDefault(shift.getDeletedByUserId(), null);

        return ShiftResponse.fromDomain(shift, createdByName, updatedByName, deletedByName);
    }

    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }
}
