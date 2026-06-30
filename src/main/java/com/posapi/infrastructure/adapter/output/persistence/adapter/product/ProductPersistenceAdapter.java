package com.posapi.infrastructure.adapter.output.persistence.adapter.product;

import com.posapi.domain.model.product.Product;
import com.posapi.domain.repository.product.ProductRepository;
import com.posapi.infrastructure.adapter.output.persistence.mapper.product.ProductPersistenMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.product.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductPersistenceAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final ProductPersistenMapper productMapper;

    @Override
    public Product save(Product product) {
        log.debug("Saving product with SKU: {}", product.getSku());
        var entity = productMapper.toEntity(product);
        var savedEntity = productJpaRepository.save(entity);
        log.info("Successfully saved product with ID: {}", savedEntity.getId());
        return productMapper.toDomain(savedEntity);
    }


    @Override
    public Optional<Product> findById(UUID id) {
        log.debug("Finding product by ID: {}", id);
        return productJpaRepository.findById(id).map(productMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        log.debug("Finding all products");
        return productJpaRepository.findAll().stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        log.warn("Soft-deleting product by ID: {}", id);
        productJpaRepository.softDeleteById(id);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        log.debug("Finding product by SKU: {}", sku);
        return productJpaRepository.findBySku(sku).map(productMapper::toDomain);
    }

    @Override
    public List<Product> findByProductNameAndCategory(String name, String category) {
        return productJpaRepository.findByProductNameAndCategoryName(name, category)
                .stream()
                .map(productMapper::toDomain)
                .toList();
    }
}