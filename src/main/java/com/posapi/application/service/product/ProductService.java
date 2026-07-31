package com.posapi.application.service.product;

import com.posapi.application.port.product.ProductManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.exception.ResourceNotFoundException;
import com.posapi.domain.model.product.Product;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.CategoryRepository;
import com.posapi.domain.port.output.ProductRepository;
import com.posapi.domain.port.output.SupplierRepository;
import com.posapi.domain.port.output.TaxRepository;
import com.posapi.domain.port.output.UserRepository;

import com.posapi.infrastructure.security.SecurityContextHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements ProductManagementPort {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final TaxRepository taxRepository;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;

    @Transactional
    @Override
    public Product createProduct(Product productToCreate, UUID currentUserId) {
        if (productRepository.existsBySku(productToCreate.getSku())) {
            throw new DuplicateResourceException("Product with SKU '" + productToCreate.getSku() + "' already exists.");
        }

        // 1. Validar existencia de entidades relacionadas
        validateRelatedEntities(
                productToCreate.getCategoryId(),
                productToCreate.getSupplierId(),
                productToCreate.getTaxId()
        );

        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        Product newProduct = Product.createNew(
                productToCreate.getSku(),
                productToCreate.getName(),
                productToCreate.getDescription(),
                productToCreate.getPurchasePrice(),
                productToCreate.getSalePrice(),
                productToCreate.getCurrentStock(),
                productToCreate.getCategoryId(),
                productToCreate.getTaxId(),
                productToCreate.getSupplierId(),
                productToCreate.getProductType(), // PASAR productType
                currentUserId,
                currentUserRoleId
        );

        return productRepository.save(newProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(UUID id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<Product> updateProduct(UUID id, Product productChanges, UUID currentUserId) {
        // 1. Validar existencia de entidades relacionadas con el actualizar
        validateRelatedEntities(
                productChanges.getCategoryId(),
                productChanges.getSupplierId(),
                productChanges.getTaxId()
        );

        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        return productRepository.findById(id).map(existingProduct -> {
            existingProduct.updateDetails(
                    productChanges.getName(),
                    productChanges.getDescription(),
                    productChanges.getPurchasePrice(),
                    productChanges.getSalePrice(),
                    productChanges.getCategoryId(),
                    productChanges.getTaxId(),
                    productChanges.getSupplierId(),
                    productChanges.getProductType(), // PASAR productChanges.getProductType()
                    currentUserId,
                    currentUserRoleId
            );
            return productRepository.save(existingProduct);
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
    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    public Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        Set<UUID> validUserIds = userIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (validUserIds.isEmpty()) {
            return Map.of();
        }

        return userRepository.findAllById(validUserIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }

    /**
     * Válida adecuada es con las llaves foráneas existan antes de ejecutar operaciones de persistencia.
     */
    private void validateRelatedEntities(UUID categoryId, UUID supplierId, UUID taxId) {
        if (categoryId != null && !categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with ID: " + categoryId);
        }

        if (supplierId != null && !supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier not found with ID: " + supplierId);
        }

        if (taxId != null && !taxRepository.existsById(taxId)) {
            throw new ResourceNotFoundException("Tax not found with ID: " + taxId);
        }
    }
}
