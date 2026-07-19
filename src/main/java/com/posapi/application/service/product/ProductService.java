package com.posapi.application.service.product;

import com.posapi.application.port.product.ProductManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.exception.ResourceNotFoundException;
import com.posapi.domain.model.product.Product;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.ProductRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductRequest;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductResponse;
import com.posapi.infrastructure.security.SecurityContextHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements ProductManagementPort {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;

    @Transactional
    @Override
    public ProductResponse createProduct(ProductRequest request, UUID currentUserId) {
        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("Product with SKU '" + request.sku() + "' already exists.");
        }

        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        Product newProduct = Product.createNew(
                request.sku(),
                request.name(),
                request.description(),
                request.purchasePrice(),
                request.salePrice(),
                request.currentStock(),
                request.categoryId(),
                request.taxId(),
                request.supplierId(),
                currentUserId,
                currentUserRoleId
        );

        Product savedProduct = productRepository.save(newProduct);
        return mapToProductResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponse> getProductById(UUID id) {
        return productRepository.findById(id).map(this::mapToProductResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() { // CORREGIDO: Retorna List<ProductResponse>
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<ProductResponse> updateProduct(UUID id, ProductRequest request, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        return productRepository.findById(id).map(existingProduct -> {
            existingProduct.updateDetails(
                    request.name(),
                    request.description(),
                    request.purchasePrice(),
                    request.salePrice(),
                    request.categoryId(),
                    request.taxId(),
                    request.supplierId(),
                    currentUserId,
                    currentUserRoleId
            );
            Product updatedProduct = productRepository.save(existingProduct);
            return mapToProductResponse(updatedProduct);
        });
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        productRepository.findById(id).ifPresent(existingProduct -> {
            existingProduct.markAsDeleted(currentUserId, currentUserRoleId);
            productRepository.save(existingProduct);
            log.info("Product with id {} marked as deleted by user {}", id, currentUserId);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponse> getProductBySku(String sku) {
        return productRepository.findBySku(sku).map(this::mapToProductResponse);
    }

    private ProductResponse mapToProductResponse(Product product) {
        Set<UUID> userIds = Stream.of(
                product.getCreatedByUserId(),
                product.getUpdatedByUserId(),
                product.getDeletedByUserId()
        ).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<UUID, String> userNames = fetchUserNames(userIds);

        String createdByName = userNames.getOrDefault(product.getCreatedByUserId(), null);
        String updatedByName = userNames.getOrDefault(product.getUpdatedByUserId(), null);
        String deletedByName = userNames.getOrDefault(product.getDeletedByUserId(), null);

        return ProductResponse.fromDomain(product, createdByName, updatedByName, deletedByName);
    }

    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }
}
