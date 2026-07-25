package com.posapi.infrastructure.adapter.input.rest.shift;

import com.posapi.application.port.shift.ShiftManagementPort;
import com.posapi.infrastructure.adapter.input.rest.shift.dto.ShiftRequest;
import com.posapi.infrastructure.adapter.input.rest.shift.dto.ShiftResponse;
import com.posapi.infrastructure.security.SecurityContextHelper;
import com.posapi.shared.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftManagementPort shiftManagementPort;
    private final SecurityContextHelper securityContextHelper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<ShiftResponse> createShift(@Valid @RequestBody ShiftRequest request) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        ShiftResponse createdShift = shiftManagementPort.createShift(request, currentUserId);
        return new ResponseEntity<>(createdShift, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<ShiftResponse> getShiftById(@PathVariable UUID id) {
        return shiftManagementPort.getShiftById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<PageResponse<ShiftResponse>> getAllShifts(Pageable pageable) {
        PageResponse<ShiftResponse> shifts = shiftManagementPort.getAllShifts(pageable);
        return ResponseEntity.ok(shifts);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<ShiftResponse> updateShift(@PathVariable UUID id, @Valid @RequestBody ShiftRequest request) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        return shiftManagementPort.updateShift(id, request, currentUserId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<ShiftResponse> closeShift(@PathVariable UUID id, @RequestParam BigDecimal endingCash) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        return shiftManagementPort.closeShift(id, endingCash, currentUserId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> cancelShift(@PathVariable UUID id) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        shiftManagementPort.cancelShift(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteShift(@PathVariable UUID id) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        shiftManagementPort.deleteShift(id, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
