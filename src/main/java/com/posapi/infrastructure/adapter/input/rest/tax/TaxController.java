package com.posapi.infrastructure.adapter.input.rest.tax;

import com.posapi.application.port.tax.TaxManagementPort;
import com.posapi.domain.model.tax.Tax;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.tax.dto.TaxRequest;
import com.posapi.infrastructure.adapter.input.rest.tax.dto.TaxResponse;
import com.posapi.infrastructure.adapter.input.rest.tax.mapper.TaxRestMapper;
import com.posapi.infrastructure.security.SecurityContextHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
@RequestMapping("/api/v1/taxes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TaxController {

    private final TaxManagementPort taxManagementPort;
    private final TaxRestMapper taxRestMapper;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;

    @PostMapping
    public ResponseEntity<TaxResponse> createTax(@Valid @RequestBody TaxRequest request) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        Tax taxToCreate = taxRestMapper.toDomain(request);
        taxToCreate.setCreatedBy(currentUser.getId());
        Tax createdTax = taxManagementPort.createTax(taxToCreate);
        return new ResponseEntity<>(
                taxRestMapper.toResponse(createdTax, currentUser.getFullName(), null),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<TaxResponse>> getAllTaxes() {
        List<Tax> taxes = taxManagementPort.getAllTaxes();
        Set<UUID> userIds = taxes.stream()
                .flatMap(t -> Stream.of(t.getCreatedBy(), t.getUpdatedBy()))
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<UUID, String> userNames = fetchUserNames(userIds);
        return ResponseEntity.ok(taxes.stream()
                .map(t -> taxRestMapper.toResponse(
                        t, userNames.get(t.getCreatedBy()), userNames.get(t.getUpdatedBy()))
                ).collect(Collectors.toList()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaxResponse> getTaxById(@PathVariable UUID id) {
        return taxManagementPort.getTaxById(id)
                .map(this::mapToResponseWithUserNames)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaxResponse> updateTax(@PathVariable UUID id, @Valid @RequestBody TaxRequest request) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        Tax taxToUpdate = taxRestMapper.toDomain(request);
        taxToUpdate.setUpdatedBy(currentUser.getId());
        return taxManagementPort.updateTax(id, taxToUpdate)
                .map(this::mapToResponseWithUserNames)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTax(@PathVariable UUID id) {
        taxManagementPort.deleteTax(id);
        return ResponseEntity.noContent().build();
    }

    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }

    private TaxResponse mapToResponseWithUserNames(Tax tax) {
        Set<UUID> userIds = Stream.of(tax.getCreatedBy(), tax.getUpdatedBy())
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<UUID, String> userNames = fetchUserNames(userIds);
        return taxRestMapper.toResponse(
                tax, userNames.get(tax.getCreatedBy()), userNames.get(tax.getUpdatedBy())
        );
    }
}
