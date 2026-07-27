package com.posapi.application.service.product;



import com.posapi.application.port.product.ProductManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.product.Product;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.ProductRepository;
import com.posapi.domain.port.output.UserRepository;

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
    public Product createProduct(Product productToCreate, UUID currentUserId) {
        if (productRepository.existsBySku(productToCreate.getSku())) {
            throw new DuplicateResourceException("Product with SKU '" + productToCreate.getSku() + "' already exists.");
        }

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

    // Si necesitas resolver nombres de usuario para la respuesta REST,
    // esa responsabilidad de ensamble o mapeo a ProductResponse va en el REST Controller/Mapper.
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
}
