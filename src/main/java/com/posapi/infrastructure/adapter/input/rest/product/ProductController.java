package com.posapi.infrastructure.adapter.input.rest.product;

import com.posapi.application.port.product.ProductManagementPort;
import com.posapi.application.service.ai.AIService;
import com.posapi.domain.model.product.Product;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.product.dto.GenerateDescriptionRequest;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductRequest;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductResponse;
import com.posapi.infrastructure.adapter.input.rest.product.mapper.ProductRestMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductManagementPort productManagementPort;
    private final ProductRestMapper productRestMapper;
    private final UserRepository userRepository;
    private final AIService aiService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        UUID currentUserId = getCurrentUserId();

        // 1. DTO -> Domain
        Product productToCreate = productRestMapper.toDomain(request);

        // 2. Invocar puerto de la capa de aplicación
        Product createdProduct = productManagementPort.createProduct(productToCreate, currentUserId);

        // 3. Domain -> DTO
        Map<UUID, String> userNames = fetchUserNamesForProduct(createdProduct);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productRestMapper.toResponse(createdProduct, userNames));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return productManagementPort.getProductById(id)
                .map(product -> {
                    Map<UUID, String> userNames = fetchUserNamesForProduct(product);
                    return ResponseEntity.ok(productRestMapper.toResponse(product, userNames));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<Product> products = productManagementPort.getAllProducts();

        Set<UUID> userIds = products.stream()
                .flatMap(p -> Stream.of(p.getCreatedByUserId(), p.getUpdatedByUserId(), p.getDeletedByUserId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<UUID, String> userNames = fetchUserNames(userIds);

        List<ProductResponse> responseList = products.stream()
                .map(product -> productRestMapper.toResponse(product, userNames))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest request) {

        UUID currentUserId = getCurrentUserId();
        Product productChanges = productRestMapper.toDomain(request);

        return productManagementPort.updateProduct(id, productChanges, currentUserId)
                .map(updatedProduct -> {
                    Map<UUID, String> userNames = fetchUserNamesForProduct(updatedProduct);
                    return ResponseEntity.ok(productRestMapper.toResponse(updatedProduct, userNames));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        UUID currentUserId = getCurrentUserId();
        productManagementPort.deleteProduct(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        return productManagementPort.getProductBySku(sku)
                .map(product -> {
                    Map<UUID, String> userNames = fetchUserNamesForProduct(product);
                    return ResponseEntity.ok(productRestMapper.toResponse(product, userNames));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/generate-description")
    public ResponseEntity<String> generateProductDescription(@Valid @RequestBody GenerateDescriptionRequest request) {
        String description = aiService.generateProductDescription(request.productName(), request.characteristics());
        return ResponseEntity.ok(description);
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }

        // Ajusta la conversión si tu Principal almacena el ID como String o dentro de un CustomUserDetails
        Object principal = authentication.getPrincipal();
        if (principal instanceof UUID uuid) {
            return uuid;
        }

        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Map<UUID, String> fetchUserNamesForProduct(Product product) {
        Set<UUID> userIds = Stream.of(
                product.getCreatedByUserId(),
                product.getUpdatedByUserId(),
                product.getDeletedByUserId()
        ).filter(Objects::nonNull).collect(Collectors.toSet());

        return fetchUserNames(userIds);
    }

    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(
                        user -> user.getId(),
                        user -> user.getFullName()
                ));
    }
}
