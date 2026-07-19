package com.posapi.application.service.tax;

import com.posapi.application.port.tax.TaxManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.exception.ResourceNotFoundException;
import com.posapi.domain.model.tax.Tax;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.TaxRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.tax.dto.TaxRequest;
import com.posapi.infrastructure.adapter.input.rest.tax.dto.TaxResponse;
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
public class TaxService implements TaxManagementPort {

    private final TaxRepository taxRepository;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;

    @Override
    @Transactional
    public TaxResponse createTax(TaxRequest request, UUID currentUserId) {
        if (taxRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Tax with name '" + request.name() + "' already exists.");
        }

        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        Tax newTax = Tax.createNew(
                request.name(),
                request.percentage(),
                request.taxType(),
                currentUserId,
                currentUserRoleId
        );

        Tax savedTax = taxRepository.save(newTax);
        return mapToTaxResponse(savedTax);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaxResponse> getTaxById(UUID id) {
        return taxRepository.findById(id).map(this::mapToTaxResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaxResponse> getAllTaxes() {
        List<Tax> taxes = taxRepository.findAll();
        return taxes.stream()
                .map(this::mapToTaxResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<TaxResponse> updateTax(UUID id, TaxRequest request, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        return taxRepository.findById(id).map(existingTax -> {
            if (request.name() != null && !request.name().equals(existingTax.getName())) {
                if (taxRepository.existsByName(request.name())) {
                    throw new DuplicateResourceException("Tax with name '" + request.name() + "' already exists.");
                }
                existingTax.updateDetails(
                        request.name(),
                        request.percentage(),
                        request.taxType(),
                        currentUserId, // updatedByUserId
                        currentUserRoleId // updatedByRoleId
                );
            } else {
                existingTax.updateDetails(
                        existingTax.getName(), // Keep existing name
                        request.percentage(),
                        request.taxType(),
                        currentUserId, // updatedByUserId
                        currentUserRoleId // updatedByRoleId
                );
            }
            Tax updatedTax = taxRepository.save(existingTax);
            return mapToTaxResponse(updatedTax);
        });
    }

    @Override
    @Transactional
    public void deleteTax(UUID id, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        taxRepository.findById(id).ifPresent(existingTax -> {
            existingTax.markAsDeleted(currentUserId, currentUserRoleId);
            taxRepository.save(existingTax);
            log.info("Tax with id {} marked as deleted by user {}", id, currentUserId);
        });
    }

    private TaxResponse mapToTaxResponse(Tax tax) {
        Set<UUID> userIds = Stream.of(
                tax.getCreatedByUserId(),
                tax.getUpdatedByUserId(),
                tax.getDeletedByUserId()
        ).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<UUID, String> userNames = fetchUserNames(userIds);

        String createdByName = userNames.getOrDefault(tax.getCreatedByUserId(), null);
        String updatedByName = userNames.getOrDefault(tax.getUpdatedByUserId(), null);
        String deletedByName = userNames.getOrDefault(tax.getDeletedByUserId(), null);

        return TaxResponse.fromDomain(tax, createdByName, updatedByName);
    }

    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }
}
