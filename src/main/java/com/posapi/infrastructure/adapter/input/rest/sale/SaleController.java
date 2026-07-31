package com.posapi.infrastructure.adapter.input.rest.sale;

import com.posapi.application.port.sale.SaleMagnamentPort;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleItemRequest;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleRequest;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleMagnamentPort saleMagnamentPort;

    public SaleController(SaleMagnamentPort saleMagnamentPort) {
        this.saleMagnamentPort = saleMagnamentPort;
    }

    @PostMapping
    public ResponseEntity<SaleResponse> createSale(@RequestBody SaleRequest restRequest) {
        SaleResponse response = saleMagnamentPort.createSale(restRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> getSaleById(@PathVariable UUID id) {
        SaleResponse response = saleMagnamentPort.getSaleById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleResponse> updateSale(@PathVariable UUID id, @RequestBody SaleRequest restRequest) {
        SaleResponse response = saleMagnamentPort.updateSale(id, restRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable UUID id) {
        saleMagnamentPort.deleteSale(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{saleId}/items")
    public ResponseEntity<SaleResponse> addSaleItem(@PathVariable UUID saleId, @RequestBody SaleItemRequest restItemRequest) {
        SaleResponse response = saleMagnamentPort.addSaleItem(saleId, restItemRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{saleId}/items/{itemId}")
    public ResponseEntity<SaleResponse> updateSaleItem(@PathVariable UUID saleId, @PathVariable UUID itemId, @RequestBody SaleItemRequest restItemRequest) {
        SaleResponse response = saleMagnamentPort.updateSaleItem(saleId, itemId, restItemRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{saleId}/items/{itemId}")
    public ResponseEntity<SaleResponse> deleteSaleItem(@PathVariable UUID saleId, @PathVariable UUID itemId) {
        SaleResponse response = saleMagnamentPort.deleteSaleItem(saleId, itemId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SaleResponse>> getAllSales() {
        List<SaleResponse> responses = saleMagnamentPort.getAllSales();
        return ResponseEntity.ok(responses);
    }
}
