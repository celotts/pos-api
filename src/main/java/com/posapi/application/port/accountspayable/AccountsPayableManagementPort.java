package com.posapi.application.port.accountspayable;

import com.posapi.infrastructure.adapter.input.rest.accountspayable.dto.AccountsPayableRequest;
import com.posapi.infrastructure.adapter.input.rest.accountspayable.dto.AccountsPayableResponse;
import com.posapi.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface AccountsPayableManagementPort {

    AccountsPayableResponse createAccountsPayable(AccountsPayableRequest request, UUID currentUserId);

    Optional<AccountsPayableResponse> getAccountsPayableById(UUID id);

    PageResponse<AccountsPayableResponse> getAllAccountsPayable(Pageable pageable);

    PageResponse<AccountsPayableResponse> getAccountsPayableBySupplier(UUID supplierId, Pageable pageable);

    // CORREGIDO: Ahora devuelve Optional
    Optional<AccountsPayableResponse> updateAccountsPayable(UUID id, AccountsPayableRequest request, UUID currentUserId);

    // CORREGIDO: Ahora devuelve Optional
    Optional<AccountsPayableResponse> markAsPaid(UUID id, BigDecimal amountPaid, UUID currentUserId);

    void deleteAccountsPayable(UUID id, UUID currentUserId);

    PageResponse<AccountsPayableResponse> getOverdueAccountsPayable(LocalDate asOfDate, Pageable pageable);
}
