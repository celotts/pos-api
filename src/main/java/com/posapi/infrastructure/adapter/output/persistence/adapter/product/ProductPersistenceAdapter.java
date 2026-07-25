package com.posapi.infrastructure.adapter.output.persistence.adapter.product;

import com.posapi.domain.model.product.Product;
import com.posapi.domain.port.output.ProductRepository;
import com.posapi.infrastructure.adapter.output.persistence.entity.product.ProductEntity;
import com.posapi.infrastructure.adapter.output.persistence.mapper.product.ProductPersistenceMapper;
import com.posapi.infrastructure.adapter.output.persistence.repository.product.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final ProductPersistenceMapper productMapper;

    @Override
    public Product save(Product product) {
        ProductEntity entity = productMapper.toEntity(product);
        ProductEntity savedEntity = productJpaRepository.save(entity);
        return productMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productJpaRepository.findById(id).map(productMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream().map(productMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        productJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsBySku(String sku) {
        return productJpaRepository.existsBySku(sku);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return productJpaRepository.findBySku(sku).map(productMapper::toDomain);
    }

    @Override
    public Optional<Product> updateStock(UUID productId, BigDecimal quantityChange) {
        return productJpaRepository.findById(productId)
                .map(productEntity -> {
                    Product product = productMapper.toDomain(productEntity);
                    if (quantityChange.compareTo(BigDecimal.ZERO) > 0) {
                        product.increaseStock(quantityChange, product.getUpdatedByUserId(), product.getUpdatedByUserRoleId());
                    } else if (quantityChange.compareTo(BigDecimal.ZERO) < 0) {
                        product.decreaseStock(quantityChange.abs(), product.getUpdatedByUserId(), product.getUpdatedByUserRoleId());
                    }

                    ProductEntity updatedProductEntity = productMapper.toEntity(product);
                    return productMapper.toDomain(productJpaRepository.save(updatedProductEntity));
                });
    }
}
