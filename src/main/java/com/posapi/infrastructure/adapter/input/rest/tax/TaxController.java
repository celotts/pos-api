package com.posapi.infrastructure.adapter.input.rest.tax;

import com.posapi.application.port.tax.TaxManagementPort;
import com.posapi.domain.model.tax.Tax;
import com.posapi.infrastructure.adapter.input.rest.tax.dto.TaxRequest;
import com.posapi.infrastructure.adapter.input.rest.tax.mapper.TaxRestMapper;
import com.posapi.infrastructure.adapter.input.rest.tax.dto.TaxResponse; // Import TaxResponse
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/taxes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TaxController {

    private final TaxManagementPort taxManagementPort;
    private final TaxRestMapper taxRestMapper; // Still needed for toDomain(TaxRequest)

    @PostMapping
    public ResponseEntity<TaxResponse> createTax(@Valid @RequestBody TaxRequest request) {
        // 🛡️ World-Class: Controller delegates all business logic to the service.
        // The service will handle setting createdBy, ID, and enrichment.
        Tax taxToCreate = taxRestMapper.toDomain(request);
        Tax createdTax = taxManagementPort.createTax(taxToCreate);
        // After creation, fetch the enriched version for the response
        return taxManagementPort.getTaxById(createdTax.getId()) // Use getTaxById which now returns Optional<TaxResponse>
                .map(responseDto -> new ResponseEntity<>(responseDto, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()); // Should not happen
    }

    @GetMapping
    public ResponseEntity<List<TaxResponse>> getAllTaxes() {
        // 🛡️ World-Class: Service now returns enriched DTOs directly.
        return ResponseEntity.ok(taxManagementPort.getAllTaxes());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaxResponse> getTaxById(@PathVariable UUID id) {
        // 🛡️ World-Class: Service now returns enriched DTO directly.
        return taxManagementPort.getTaxById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaxResponse> updateTax(@PathVariable UUID id, @Valid @RequestBody TaxRequest request) {
        // 🛡️ World-Class: Controller delegates all business logic to the service.
        // The service will handle setting updatedBy and enrichment.
        Tax taxToUpdate = taxRestMapper.toDomain(request);
        return taxManagementPort.updateTax(id, taxToUpdate)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTax(@PathVariable UUID id) {
        taxManagementPort.deleteTax(id);
        return ResponseEntity.noContent().build();
    }
}
