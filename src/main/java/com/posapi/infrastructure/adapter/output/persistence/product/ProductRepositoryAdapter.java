package com.posapi.infrastructure.adapter.output.persistence.product;

import com.posapi.domain.model.product.Product; // Actualizado
import com.posapi.domain.repository.product.ProductRepository; // Actualizado
import com.posapi.infrastructure.persistence.entity.product.ProductEntity; // Actualizado
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    public ProductRepositoryAdapter(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public Product save(Product product) {
        ProductEntity productEntity = toEntity(product);
        ProductEntity savedEntity = productJpaRepository.save(productEntity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        productJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return productJpaRepository.findBySku(sku).map(this::toDomain);
    }

    @Override
    public List<Product> findByProductNameAndCategory(String name, String category) {
        return List.of();
    }

    // --- Mappers ---
    private ProductEntity toEntity(Product product) {
        return ProductEntity.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .purchasePrice(product.getPurchasePrice())
                .salePrice(product.getSalePrice())
                .currentStock(product.getCurrentStock())
                .taxId(product.getTaxId())
                .supplierId(product.getSupplierId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .deletedAt(product.getDeletedAt())
                .createdByUserId(product.getCreatedByUserId())
                .updatedByUserId(product.getUpdatedByUserId())
                .deletedByUserId(product.getDeletedByUserId())
                .build();
    }

    private Product toDomain(ProductEntity productEntity) {
        return Product.builder()
                .id(productEntity.getId())
                .sku(productEntity.getSku())
                .name(productEntity.getName())
                .description(productEntity.getDescription())
                .purchasePrice(productEntity.getPurchasePrice())
                .salePrice(productEntity.getSalePrice())
                .currentStock(productEntity.getCurrentStock())
                .taxId(productEntity.getTaxId())
                .supplierId(productEntity.getSupplierId())
                .createdAt(productEntity.getCreatedAt())
                .updatedAt(productEntity.getUpdatedAt())
                .deletedAt(productEntity.getDeletedAt())
                .createdByUserId(productEntity.getCreatedByUserId())
                .updatedByUserId(productEntity.getUpdatedByUserId())
                .deletedByUserId(productEntity.getDeletedByUserId())
                .build();
    }
}
