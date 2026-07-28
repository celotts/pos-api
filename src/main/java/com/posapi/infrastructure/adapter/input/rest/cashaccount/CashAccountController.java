package com.posapi.infrastructure.adapter.input.rest.cashaccount;

import com.posapi.application.port.cashaccount.CashAccountManagementPort;
import com.posapi.infrastructure.adapter.input.rest.cashaccount.dto.CashAccountRequest;
import com.posapi.infrastructure.adapter.input.rest.cashaccount.dto.CashAccountResponse;
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
@RequestMapping("/api/v1/cash-accounts")
@RequiredArgsConstructor
public class CashAccountController {

    private final CashAccountManagementPort cashAccountManagementPort;
    private final SecurityContextHelper securityContextHelper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CashAccountResponse> createCashAccount(@Valid @RequestBody CashAccountRequest request) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        CashAccountResponse createdAccount = cashAccountManagementPort.createCashAccount(request, currentUserId);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<CashAccountResponse> getCashAccountById(@PathVariable UUID id) {
        return cashAccountManagementPort.getCashAccountById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<List<CashAccountResponse>> getAllCashAccounts() {
        List<CashAccountResponse> accounts = cashAccountManagementPort.getAllCashAccounts();
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CashAccountResponse> updateCashAccount(@PathVariable UUID id, @Valid @RequestBody CashAccountRequest request) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        return cashAccountManagementPort.updateCashAccount(id, request, currentUserId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteCashAccount(@PathVariable UUID id) {
        UUID currentUserId = securityContextHelper.getCurrentUserId();
        cashAccountManagementPort.deleteCashAccount(id, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
