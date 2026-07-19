package com.posapi.application.service.supplier;

import com.posapi.application.port.supplier.SupplierManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.exception.ResourceNotFoundException;
import com.posapi.domain.model.supplier.Supplier;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.SupplierRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.supplier.dto.SupplierRequest;
import com.posapi.infrastructure.adapter.input.rest.supplier.dto.SupplierResponse;
import com.posapi.infrastructure.security.SecurityContextHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set; // AÑADIDO: Importar Set
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierService implements SupplierManagementPort {

    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;

    @Override
    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request, UUID currentUserId) {
        if (supplierRepository.existsByRfc(request.rfc())) {
            throw new DuplicateResourceException("Supplier with RFC '" + request.rfc() + "' already exists.");
        }

        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        Supplier newSupplier = Supplier.createNew(
                request.rfc(),
                request.businessName(),
                request.taxRegimen(),
                request.contactEmail(),
                currentUserId,
                currentUserRoleId
        );

        Supplier savedSupplier = supplierRepository.save(newSupplier);
        return mapToSupplierResponse(savedSupplier);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplierResponse> getSupplierById(UUID id) {
        return supplierRepository.findById(id).map(this::mapToSupplierResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();
        return suppliers.stream()
                .map(this::mapToSupplierResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<SupplierResponse> updateSupplier(UUID id, SupplierRequest request, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        return supplierRepository.findById(id).map(existingSupplier -> {
            if (request.rfc() != null && !request.rfc().equals(existingSupplier.getRfc())) {
                if (supplierRepository.existsByRfc(request.rfc())) {
                    throw new DuplicateResourceException("Supplier with RFC '" + request.rfc() + "' already exists.");
                }
            }
            existingSupplier.updateDetails(
                    request.rfc(),
                    request.businessName(),
                    request.taxRegimen(),
                    request.contactEmail(),
                    currentUserId,
                    currentUserRoleId
            );
            Supplier updatedSupplier = supplierRepository.save(existingSupplier);
            return mapToSupplierResponse(updatedSupplier);
        });
    }

    @Override
    @Transactional
    public void deleteSupplier(UUID id, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        supplierRepository.findById(id).ifPresent(existingSupplier -> {
            existingSupplier.markAsDeleted(currentUserId, currentUserRoleId);
            supplierRepository.save(existingSupplier);
            log.info("Supplier with id {} marked as deleted by user {}", id, currentUserId);
        });
    }

    private SupplierResponse mapToSupplierResponse(Supplier supplier) {
        Set<UUID> userIds = Stream.of(
                supplier.getCreatedByUserId(),
                supplier.getUpdatedByUserId(),
                supplier.getDeletedByUserId()
        ).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<UUID, String> userNames = fetchUserNames(userIds);

        String createdByName = userNames.getOrDefault(supplier.getCreatedByUserId(), null);
        String updatedByName = userNames.getOrDefault(supplier.getUpdatedByUserId(), null);
        String deletedByName = userNames.getOrDefault(supplier.getDeletedByUserId(), null);

        return SupplierResponse.fromDomain(supplier, createdByName, updatedByName, deletedByName);
    }

    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }
}
