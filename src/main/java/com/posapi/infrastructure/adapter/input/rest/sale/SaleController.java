package com.posapi.infrastructure.adapter.input.rest.sale;

import com.posapi.application.port.sale.SaleMagnamentPort;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleResponse;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleRequest;
import com.posapi.infrastructure.adapter.input.rest.sale.mapper.SaleRestMapper;
import com.posapi.infrastructure.adapter.input.rest.saleItem.dto.SaleItemRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleMagnamentPort saleMagnamentPort;
    private final SaleRestMapper mapper;

    public SaleController(SaleMagnamentPort saleMagnamentPort, SaleRestMapper mapper) {
        this.saleMagnamentPort = saleMagnamentPort;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<SaleResponse> createSale(@RequestBody SaleRequest restRequest) {
        // En este caso, los DTOs de REST y de la capa de aplicación son los mismos,
        // pero el mapper proporciona la capa de abstracción.
        SaleResponse applicationResponse = saleMagnamentPort.createSale(restRequest);
        return new ResponseEntity<>(mapper.toRestSaleResponse(applicationResponse), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> getSaleById(@PathVariable UUID id) {
        SaleResponse applicationResponse = saleMagnamentPort.getSaleById(id);
        return ResponseEntity.ok(mapper.toRestSaleResponse(applicationResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleResponse> updateSale(@PathVariable UUID id, @RequestBody SaleRequest restRequest) {
        SaleResponse applicationResponse = saleMagnamentPort.updateSale(id, restRequest);
        return ResponseEntity.ok(mapper.toRestSaleResponse(applicationResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable UUID id) {
        saleMagnamentPort.deleteSale(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{saleId}/items")
    public ResponseEntity<SaleResponse> addSaleItem(@PathVariable UUID saleId, @RequestBody SaleItemRequest restItemRequest) {
        SaleResponse applicationResponse = saleMagnamentPort.addSaleItem(saleId, restItemRequest);
        return ResponseEntity.ok(mapper.toRestSaleResponse(applicationResponse));
    }

    @PutMapping("/{saleId}/items/{itemId}")
    public ResponseEntity<SaleResponse> updateSaleItem(@PathVariable UUID saleId, @PathVariable UUID itemId, @RequestBody SaleItemRequest restItemRequest) {
        SaleResponse applicationResponse = saleMagnamentPort.updateSaleItem(saleId, itemId, restItemRequest);
        return ResponseEntity.ok(mapper.toRestSaleResponse(applicationResponse));
    }

    @DeleteMapping("/{saleId}/items/{itemId}")
    public ResponseEntity<SaleResponse> deleteSaleItem(@PathVariable UUID saleId, @PathVariable UUID itemId) {
        SaleResponse applicationResponse = saleMagnamentPort.deleteSaleItem(saleId, itemId);
        return ResponseEntity.ok(mapper.toRestSaleResponse(applicationResponse));
    }

    @GetMapping
    public ResponseEntity<List<SaleResponse>> getAllSales() {
        List<SaleResponse> applicationResponses = saleMagnamentPort.getAllSales();
        return ResponseEntity.ok(mapper.toRestSaleResponseList(applicationResponses));
    }
}
