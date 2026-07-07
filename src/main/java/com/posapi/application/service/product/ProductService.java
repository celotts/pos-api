package com.posapi.application.service.product;

import com.posapi.application.port.product.ProductManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.product.Product;
import com.posapi.domain.port.output.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductManagementPort {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Product createProduct(Product product) {
        if (productRepository.existsBySku(product.getSku())) {
            throw new DuplicateResourceException("Product with SKU '" + product.getSku() + "' already exists.");
        }
        product.setId(UUID.randomUUID());
        return productRepository.save(product);
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
    public Optional<Product> updateProduct(UUID id, Product product) {
        return productRepository.findById(id)
                .map(existing -> {
                    existing.setName(product.getName());
                    existing.setDescription(product.getDescription());
                    existing.setPurchasePrice(product.getPurchasePrice());
                    existing.setSalePrice(product.getSalePrice());
                    existing.setCategoryId(product.getCategoryId());
                    existing.setTaxId(product.getTaxId());
                    existing.setSupplierId(product.getSupplierId());
                    existing.setUpdatedBy(product.getUpdatedBy());
                    return productRepository.save(existing);
                });
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }
}
