package com.posapi.infrastructure.adapter.input.rest.accountspayable;

import com.posapi.application.port.accountspayable.AccountsPayableManagementPort;
import com.posapi.infrastructure.adapter.input.rest.accountspayable.dto.AccountsPayableRequest;
import com.posapi.infrastructure.adapter.input.rest.accountspayable.dto.AccountsPayableResponse;
import com.posapi.infrastructure.security.SecurityContextHelper;
import com.posapi.shared.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts-payable")
@RequiredArgsConstructor
public class AccountsPayableController {

    private final AccountsPayableManagementPort accountsPayableManagementPort;
    private final SecurityContextHelper securityContextHelper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ACCOUNTANT')")
    public ResponseEntity<AccountsPayableResponse> createAccountsPayable(@Valid @RequestBody AccountsPayableRequest request) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        AccountsPayableResponse createdAccountsPayable = accountsPayableManagementPort.createAccountsPayable(request, currentUserId);
        return new ResponseEntity<>(createdAccountsPayable, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ACCOUNTANT')")
    public ResponseEntity<AccountsPayableResponse> getAccountsPayableById(@PathVariable UUID id) {
        return accountsPayableManagementPort.getAccountsPayableById(id)
                .map(ResponseEntity::ok) // CORREGIDO
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ACCOUNTANT')")
    public ResponseEntity<PageResponse<AccountsPayableResponse>> getAllAccountsPayable(@PageableDefault(size = 10, sort = "dueDate") Pageable pageable) {
        PageResponse<AccountsPayableResponse> accountsPayable = accountsPayableManagementPort.getAllAccountsPayable(pageable);
        return ResponseEntity.ok(accountsPayable);
    }

    @GetMapping("/supplier/{supplierId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ACCOUNTANT')")
    public ResponseEntity<PageResponse<AccountsPayableResponse>> getAccountsPayableBySupplier(@PathVariable UUID supplierId, @PageableDefault(size = 10, sort = "dueDate") Pageable pageable) {
        PageResponse<AccountsPayableResponse> accountsPayable = accountsPayableManagementPort.getAccountsPayableBySupplier(supplierId, pageable);
        return ResponseEntity.ok(accountsPayable);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ACCOUNTANT')")
    public ResponseEntity<PageResponse<AccountsPayableResponse>> getOverdueAccountsPayable(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate, @PageableDefault(size = 10, sort = "dueDate") Pageable pageable) {
        LocalDate date = (asOfDate != null) ? asOfDate : LocalDate.now();
        PageResponse<AccountsPayableResponse> accountsPayable = accountsPayableManagementPort.getOverdueAccountsPayable(date, pageable);
        return ResponseEntity.ok(accountsPayable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ACCOUNTANT')")
    public ResponseEntity<AccountsPayableResponse> updateAccountsPayable(@PathVariable UUID id, @Valid @RequestBody AccountsPayableRequest request) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        return accountsPayableManagementPort.updateAccountsPayable(id, request, currentUserId)
                .map(response -> ResponseEntity.ok(response)) // CORREGIDO
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PutMapping("/{id}/mark-paid")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ACCOUNTANT')")
    public ResponseEntity<AccountsPayableResponse> markAsPaid(@PathVariable UUID id, @RequestParam BigDecimal amountPaid) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        return accountsPayableManagementPort.markAsPaid(id, amountPaid, currentUserId)
                .map(response -> ResponseEntity.ok(response)) // CORREGIDO
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ACCOUNTANT')")
    public ResponseEntity<Void> deleteAccountsPayable(@PathVariable UUID id) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        accountsPayableManagementPort.deleteAccountsPayable(id, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
