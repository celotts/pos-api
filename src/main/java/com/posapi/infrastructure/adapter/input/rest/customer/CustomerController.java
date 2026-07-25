package com.posapi.infrastructure.adapter.input.rest.customer;

import com.posapi.application.port.customer.CustomerManagementPort; // CORREGIDO
import com.posapi.infrastructure.adapter.input.rest.customer.dto.CustomerRequest;
import com.posapi.infrastructure.adapter.input.rest.customer.dto.CustomerResponse;
import com.posapi.infrastructure.security.SecurityContextHelper;
import com.posapi.shared.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerManagementPort customerManagementPort; // CORREGIDO
    private final SecurityContextHelper securityContextHelper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        CustomerResponse createdCustomer = customerManagementPort.createCustomer(request, currentUserId); // CORREGIDO
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable UUID id) {
        return customerManagementPort.getCustomerById(id) // CORREGIDO
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<PageResponse<CustomerResponse>> getAllCustomers(Pageable pageable) {
        PageResponse<CustomerResponse> customers = customerManagementPort.getAllCustomers(pageable); // CORREGIDO
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable UUID id, @Valid @RequestBody CustomerRequest request) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        return customerManagementPort.updateCustomer(id, request, currentUserId) // CORREGIDO
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        customerManagementPort.deleteCustomer(id, currentUserId); // CORREGIDO
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rfc/{rfc}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<CustomerResponse> getCustomerByRfc(@PathVariable String rfc) {
        return customerManagementPort.getCustomerByRfc(rfc) // CORREGIDO
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
