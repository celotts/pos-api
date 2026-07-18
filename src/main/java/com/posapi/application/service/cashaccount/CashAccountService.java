package com.posapi.application.service.cashAccount;

import com.posapi.application.port.cashaccount.CashAccountManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.cashaccount.CashAccount;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.CashAccountRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.cashAccount.dto.CashAccountRequest;
import com.posapi.infrastructure.adapter.input.rest.cashAccount.dto.CashAccountResponse;
import com.posapi.infrastructure.adapter.input.rest.cashAccount.mapper.CashAccountRestMapper;
import com.posapi.infrastructure.security.SecurityContextHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class CashAccountService implements CashAccountManagementPort {

    private final CashAccountRepository cashAccountRepository;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;
    private final CashAccountRestMapper cashAccountRestMapper;

    @Override
    @Transactional
    public CashAccountResponse createCashAccount(CashAccountRequest request, UUID currentUserId) {
        if (cashAccountRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Cash Account with name '" + request.name() + "' already exists.");
        }

        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        CashAccount newCashAccount = CashAccount.createNew(
                request.name(),
                request.accountType(),
                request.initialBalance(),
                request.currency(),
                currentUserId,
                currentUserRoleId
        );

        CashAccount savedCashAccount = cashAccountRepository.save(newCashAccount);
        return mapToCashAccountResponse(savedCashAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CashAccountResponse> getCashAccountById(UUID id) {
        return cashAccountRepository.findById(id).map(this::mapToCashAccountResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CashAccountResponse> getAllCashAccounts() {
        List<CashAccount> cashAccounts = cashAccountRepository.findAll();
        return cashAccounts.stream()
                .map(this::mapToCashAccountResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<CashAccountResponse> updateCashAccount(UUID id, CashAccountRequest request, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        return cashAccountRepository.findById(id).map(existingCashAccount -> {
            if (request.name() != null && !request.name().equals(existingCashAccount.getName())) {
                if (cashAccountRepository.existsByName(request.name())) {
                    throw new DuplicateResourceException("Cash Account with name '" + request.name() + "' already exists.");
                }
            }
            existingCashAccount.updateName(request.name(), currentUserId, currentUserRoleId);
            // Otros campos a actualizar si es necesario
            // existingCashAccount.setAccountType(request.accountType());
            // existingCashAccount.setCurrency(request.currency());

            CashAccount updatedCashAccount = cashAccountRepository.save(existingCashAccount);
            return mapToCashAccountResponse(updatedCashAccount);
        });
    }

    @Override
    @Transactional
    public void deleteCashAccount(UUID id, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        cashAccountRepository.findById(id).ifPresent(existingCashAccount -> {
            existingCashAccount.markAsDeleted(currentUserId, currentUserRoleId);
            cashAccountRepository.save(existingCashAccount);
            log.info("Cash Account with id {} marked as deleted by user {}", id, currentUserId);
        });
    }

    private CashAccountResponse mapToCashAccountResponse(CashAccount cashAccount) {
        Set<UUID> userIds = Stream.of(
                cashAccount.getCreatedByUserId(),
                cashAccount.getUpdatedByUserId(),
                cashAccount.getDeletedByUserId()
        ).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<UUID, String> userNames = fetchUserNames(userIds);

        String createdByName = userNames.getOrDefault(cashAccount.getCreatedByUserId(), null);
        String updatedByName = userNames.getOrDefault(cashAccount.getUpdatedByUserId(), null);
        String deletedByName = userNames.getOrDefault(cashAccount.getDeletedByUserId(), null);

        return CashAccountResponse.fromDomain(cashAccount, createdByName, updatedByName, deletedByName);
    }

    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }
}
