package com.posapi.infrastructure.adapter.input.rest.supplier;

import com.posapi.application.port.supplier.SupplierManagementPort;
import com.posapi.domain.model.supplier.Supplier;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.supplier.dto.SupplierRequest;
import com.posapi.infrastructure.adapter.input.rest.supplier.dto.SupplierResponse;
import com.posapi.infrastructure.adapter.input.rest.supplier.mapper.SupplierRestMapper;
import com.posapi.infrastructure.security.SecurityContextHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierManagementPort supplierManagementPort;
    private final SupplierRestMapper supplierRestMapper;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SupplierResponse> createSupplier(@Valid @RequestBody SupplierRequest request) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        Supplier supplierToCreate = supplierRestMapper.toDomain(request);
        supplierToCreate.setCreatedBy(currentUser.getId());
        Supplier createdSupplier = supplierManagementPort.createSupplier(supplierToCreate);
        return new ResponseEntity<>(
                supplierRestMapper.toResponse(createdSupplier, currentUser.getFullName(), null),
                HttpStatus.CREATED
        );
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
        List<Supplier> suppliers = supplierManagementPort.getAllSuppliers();
        Set<UUID> userIds = suppliers.stream()
                .flatMap(s -> Stream.of(s.getCreatedBy(), s.getUpdatedBy()))
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<UUID, String> userNames = fetchUserNames(userIds);
        List<SupplierResponse> responses = suppliers.stream()
                .map(s -> supplierRestMapper.toResponse(
                        s, userNames.get(s.getCreatedBy()), userNames.get(s.getUpdatedBy()))
                ).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }
}
