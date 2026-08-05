package com.posapi.infrastructure.adapter.input.rest.purchase;

import com.posapi.application.port.purchase.PurchaseManagementPort;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseRequest;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseResponse;
import com.posapi.infrastructure.security.SecurityContextHelper;
import com.posapi.shared.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseManagementPort purchaseManagementPort;
    private final SecurityContextHelper securityContextHelper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASING')")
    public ResponseEntity<PurchaseResponse> createPurchase(@Valid @RequestBody PurchaseRequest request) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        PurchaseResponse createdPurchase = purchaseManagementPort.createPurchase(request, currentUserId);
        return new ResponseEntity<>(createdPurchase, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASING', 'CASHIER')")
    public ResponseEntity<PurchaseResponse> getPurchaseById(@PathVariable UUID id) {
        return purchaseManagementPort.getPurchaseById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASING', 'CASHIER')")
    public ResponseEntity<PageResponse<PurchaseResponse>> getAllPurchases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<PurchaseResponse> purchases = purchaseManagementPort.getAllPurchases(pageable);
        return ResponseEntity.ok(purchases);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASING')")
    public ResponseEntity<PurchaseResponse> updatePurchase(@PathVariable UUID id, @Valid @RequestBody PurchaseRequest request) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        return purchaseManagementPort.updatePurchase(id, request, currentUserId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASING')")
    public ResponseEntity<Void> deletePurchase(@PathVariable UUID id) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        purchaseManagementPort.deletePurchase(id, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
