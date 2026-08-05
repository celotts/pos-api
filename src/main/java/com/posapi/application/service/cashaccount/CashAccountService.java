package com.posapi.application.service.cashaccount;

import com.posapi.application.port.cashaccount.CashAccountManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.cashaccount.CashAccount;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.CashAccountRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.cashaccount.dto.CashAccountRequest;
import com.posapi.infrastructure.adapter.input.rest.cashaccount.dto.CashAccountResponse;
import com.posapi.infrastructure.adapter.input.rest.cashaccount.mapper.CashAccountRestMapper;
import com.posapi.infrastructure.security.SecurityContextHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        if (cashAccountRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Cash Account with name '" + request.getName() + "' already exists.");
        }

        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        CashAccount newCashAccount = CashAccount.createNew(
                request.getName(),
                request.getAccountType(),
                request.getInitialBalance(),
                request.getCurrency(),
                currentUserId,
                currentUserRoleId
        );

        CashAccount savedCashAccount = cashAccountRepository.save(newCashAccount);
        return cashAccountRestMapper.toResponse(savedCashAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CashAccountResponse> getCashAccountById(UUID id) {
        return cashAccountRepository.findById(id).map(cashAccountRestMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CashAccountResponse> getAllCashAccounts() {
        return cashAccountRepository.findAll().stream()
                .map(cashAccountRestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<CashAccountResponse> updateCashAccount(UUID id, CashAccountRequest request, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        return cashAccountRepository.findById(id).map(existingCashAccount -> {
            if (request.getName() != null && !request.getName().equals(existingCashAccount.getName())) {
                if (cashAccountRepository.existsByName(request.getName())) {
                    throw new DuplicateResourceException("Cash Account with name '" + request.getName() + "' already exists.");
                }
                existingCashAccount.updateName(request.getName(), currentUserId, currentUserRoleId);
            }

            CashAccount updatedCashAccount = cashAccountRepository.save(existingCashAccount);
            return cashAccountRestMapper.toResponse(updatedCashAccount);
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
}
