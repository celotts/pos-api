package com.posapi.infrastructure.adapter.input.rest.tax;

import com.posapi.application.port.tax.TaxManagementPort;
import com.posapi.infrastructure.adapter.input.rest.tax.dto.TaxRequest;
import com.posapi.infrastructure.adapter.input.rest.tax.dto.TaxResponse;
import com.posapi.infrastructure.security.SecurityContextHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/taxes")
@RequiredArgsConstructor
public class TaxController {

    private final TaxManagementPort taxManagementPort;
    private final SecurityContextHelper securityContextHelper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TaxResponse> createTax(@Valid @RequestBody TaxRequest request) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        TaxResponse createdTax = taxManagementPort.createTax(request, currentUserId);
        return new ResponseEntity<>(createdTax, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<TaxResponse> getTaxById(@PathVariable UUID id) {
        return taxManagementPort.getTaxById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<List<TaxResponse>> getAllTaxes() {
        List<TaxResponse> taxes = taxManagementPort.getAllTaxes();
        return ResponseEntity.ok(taxes);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TaxResponse> updateTax(@PathVariable UUID id, @Valid @RequestBody TaxRequest request) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        return taxManagementPort.updateTax(id, request, currentUserId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteTax(@PathVariable UUID id) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        taxManagementPort.deleteTax(id, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
