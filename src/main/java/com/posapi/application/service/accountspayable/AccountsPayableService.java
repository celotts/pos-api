package com.posapi.application.service.accountspayable;

import com.posapi.application.port.accountspayable.AccountsPayableManagementPort;
import com.posapi.domain.model.accountspayable.AccountsPayable;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.AccountsPayableRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.accountspayable.dto.AccountsPayableRequest;
import com.posapi.infrastructure.adapter.input.rest.accountspayable.dto.AccountsPayableResponse;
import com.posapi.infrastructure.security.SecurityContextHelper;
import com.posapi.shared.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountsPayableService implements AccountsPayableManagementPort {

    private final AccountsPayableRepository accountsPayableRepository;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;

    @Override
    @Transactional
    public AccountsPayableResponse createAccountsPayable(AccountsPayableRequest request, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        AccountsPayable newAccountsPayable = AccountsPayable.createNew(
                request.purchaseId(),
                request.supplierId(),
                request.originalAmount(),
                request.dueDate(),
                currentUserId,
                currentUserRoleId
        );

        AccountsPayable savedAccountsPayable = accountsPayableRepository.save(newAccountsPayable);
        return mapToAccountsPayableResponse(savedAccountsPayable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AccountsPayableResponse> getAccountsPayableById(UUID id) {
        return accountsPayableRepository.findById(id)
                .map(this::mapToAccountsPayableResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AccountsPayableResponse> getAllAccountsPayable(Pageable pageable) {
        Page<AccountsPayable> accountsPayablePage = accountsPayableRepository.findAll(pageable);
        List<AccountsPayableResponse> content = accountsPayablePage.getContent().stream()
                .map(this::mapToAccountsPayableResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                content,
                accountsPayablePage.getNumber(),
                accountsPayablePage.getSize(),
                accountsPayablePage.getTotalElements(),
                accountsPayablePage.getTotalPages(),
                accountsPayablePage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AccountsPayableResponse> getAccountsPayableBySupplier(UUID supplierId, Pageable pageable) {
        Page<AccountsPayable> accountsPayablePage = accountsPayableRepository
                .findBySupplierId(supplierId, pageable); // CORREGIDO: Línea dividida
        List<AccountsPayableResponse> content = accountsPayablePage.getContent().stream()
                .map(this::mapToAccountsPayableResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                content,
                accountsPayablePage.getNumber(),
                accountsPayablePage.getSize(),
                accountsPayablePage.getTotalElements(),
                accountsPayablePage.getTotalPages(),
                accountsPayablePage.isLast()
        );
    }

    @Override
    @Transactional
    public Optional<AccountsPayableResponse> updateAccountsPayable(UUID id, AccountsPayableRequest request, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        return accountsPayableRepository.findById(id).map(existingAccountsPayable -> {
            // Aquí puedes añadir lógica para actualizar otros campos si es necesario
            // Por ejemplo, si el request incluye un nuevo supplierId o originalAmount,
            // deberías validar si la cuenta está abierta antes de permitir esos cambios.

            // Para este ejemplo, solo actualizaremos los campos de auditoría.
            existingAccountsPayable.setUpdatedAt(java.time.Instant.now());
            existingAccountsPayable.setUpdatedByUserId(currentUserId);
            existingAccountsPayable.setUpdatedByUserRoleId(currentUserRoleId);

            AccountsPayable updatedAccountsPayable = accountsPayableRepository.save(existingAccountsPayable);
            return mapToAccountsPayableResponse(updatedAccountsPayable);
        });
    }

    @Override
    @Transactional
    public Optional<AccountsPayableResponse> markAsPaid(UUID id, BigDecimal amountPaid, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        return accountsPayableRepository.findById(id).map(existingAccountsPayable -> {
            existingAccountsPayable.recordPayment(amountPaid, currentUserId, currentUserRoleId);
            AccountsPayable paidAccountsPayable = accountsPayableRepository.save(existingAccountsPayable);
            return mapToAccountsPayableResponse(paidAccountsPayable);
        });
    }

    @Override
    @Transactional
    public void deleteAccountsPayable(UUID id, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        accountsPayableRepository.findById(id).ifPresent(existingAccountsPayable -> {
            existingAccountsPayable.markAsDeleted(currentUserId, currentUserRoleId);
            accountsPayableRepository.save(existingAccountsPayable);
            log.info("POS Terminal with id {} marked as deleted by user {}", id, currentUserId);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AccountsPayableResponse> getOverdueAccountsPayable(LocalDate asOfDate, Pageable pageable) {
        Page<AccountsPayable> accountsPayablePage = accountsPayableRepository
                .findByDueDateBeforeAndStatus(asOfDate, AccountsPayable.ArApStatus.OVERDUE, pageable);
        List<AccountsPayableResponse> content = accountsPayablePage.getContent().stream()
                .map(this::mapToAccountsPayableResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                content,
                accountsPayablePage.getNumber(),
                accountsPayablePage.getSize(),
                accountsPayablePage.getTotalElements(),
                accountsPayablePage.getTotalPages(),
                accountsPayablePage.isLast()
        );
    }

    private AccountsPayableResponse mapToAccountsPayableResponse(AccountsPayable accountsPayable) {
        Set<UUID> userIds = Stream.of(
                accountsPayable.getCreatedByUserId(),
                accountsPayable.getUpdatedByUserId(),
                accountsPayable.getDeletedByUserId()
        ).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<UUID, String> userNames = fetchUserNames(userIds);

        String createdByName = userNames.getOrDefault(accountsPayable.getCreatedByUserId(), null);
        String updatedByName = userNames.getOrDefault(accountsPayable.getUpdatedByUserId(), null);
        String deletedByName = userNames.getOrDefault(
                accountsPayable.getDeletedByUserId(), null); // CORREGIDO: Línea dividida

        return AccountsPayableResponse.fromDomain(accountsPayable, createdByName, updatedByName, deletedByName);
    }

    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }
}
