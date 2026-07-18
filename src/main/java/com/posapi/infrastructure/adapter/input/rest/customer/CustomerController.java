package com.posapi.infrastructure.adapter.input.rest.customer;

import com.posapi.application.port.customer.CustomerInputPort;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerInputPort customerInputPort;
    private final SecurityContextHelper securityContextHelper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        CustomerResponse createdCustomer = customerInputPort.createCustomer(request, currentUserId);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable UUID id) {
        return customerInputPort.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<PageResponse<CustomerResponse>> getAllCustomers(Pageable pageable) {
        PageResponse<CustomerResponse> customers = customerInputPort.getAllCustomers(pageable);
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable UUID id, @Valid @RequestBody CustomerRequest request) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        return customerInputPort.updateCustomer(id, request, currentUserId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        customerInputPort.deleteCustomer(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rfc/{rfc}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<CustomerResponse> getCustomerByRfc(@PathVariable String rfc) {
        return customerInputPort.getCustomerByRfc(rfc)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
