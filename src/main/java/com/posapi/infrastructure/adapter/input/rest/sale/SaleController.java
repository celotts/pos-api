package com.posapi.infrastructure.adapter.input.rest.sale;

import com.posapi.application.port.sale.SaleMagnamentPort;
import com.posapi.domain.model.user.User;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleRequest;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleResponse;
import com.posapi.infrastructure.adapter.input.rest.saleItem.dto.SaleItemRequest;
import com.posapi.infrastructure.security.SecurityContextHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleMagnamentPort saleMagnamentPort;
    private final SecurityContextHelper securityContextHelper;

    @PostMapping
    public ResponseEntity<SaleResponse> createSale(@Valid @RequestBody SaleRequest request) {
        SaleResponse response = saleMagnamentPort.createSale(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> getSaleById(@PathVariable UUID id) {
        SaleResponse response = saleMagnamentPort.getSaleById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SaleResponse>> getAllSales() {
        List<SaleResponse> response = saleMagnamentPort.getAllSales();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<SaleResponse>> getAllSales(Pageable pageable) {
        Page<SaleResponse> response = saleMagnamentPort.getAllSales(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleResponse> updateSale(@PathVariable UUID id, @Valid @RequestBody SaleRequest request) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        SaleResponse response = saleMagnamentPort.updateSale(id, request, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable UUID id) {
        saleMagnamentPort.deleteSale(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{saleId}/items")
    public ResponseEntity<SaleResponse> addSaleItem(@PathVariable UUID saleId, @Valid @RequestBody SaleItemRequest restItemRequest) {
        SaleResponse response = saleMagnamentPort.addSaleItem(saleId, restItemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{saleId}/items/{itemId}")
    public ResponseEntity<SaleResponse> updateSaleItem(@PathVariable UUID saleId, @PathVariable UUID itemId, @Valid @RequestBody SaleItemRequest restItemRequest) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        SaleResponse response = saleMagnamentPort.updateSaleItem(saleId, itemId, restItemRequest, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{saleId}/items/{itemId}")
    public ResponseEntity<SaleResponse> deleteSaleItem(@PathVariable UUID saleId, @PathVariable UUID itemId) {
        SaleResponse response = saleMagnamentPort.deleteSaleItem(saleId, itemId);
        return ResponseEntity.ok(response);
    }
}
