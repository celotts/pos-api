package com.posapi.infrastructure.adapter.input.rest.supplier;

import com.posapi.application.port.supplier.SupplierManagementPort;
import com.posapi.infrastructure.adapter.input.rest.supplier.dto.SupplierRequest;
import com.posapi.infrastructure.adapter.input.rest.supplier.dto.SupplierResponse;
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
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierManagementPort supplierManagementPort;
    private final SecurityContextHelper securityContextHelper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<SupplierResponse> createSupplier(@Valid @RequestBody SupplierRequest request) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        SupplierResponse createdSupplier = supplierManagementPort.createSupplier(request, currentUserId);
        return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<SupplierResponse> getSupplierById(@PathVariable UUID id) {
        return supplierManagementPort.getSupplierById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
        List<SupplierResponse> suppliers = supplierManagementPort.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<SupplierResponse> updateSupplier(@PathVariable UUID id, @Valid @RequestBody SupplierRequest request) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        return supplierManagementPort.updateSupplier(id, request, currentUserId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteSupplier(@PathVariable UUID id) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        supplierManagementPort.deleteSupplier(id, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
