package com.posapi.application.service.product;

import com.posapi.domain.model.product.Product; // Actualizado
import com.posapi.domain.repository.product.ProductRepository; // Actualizado
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        // Aquí se podría añadir lógica de negocio antes de guardar
        if (product.getId() == null) {
            product.setId(UUID.randomUUID());
        }
        if (product.getCreatedAt() == null) {
            product.setCreatedAt(Instant.now());
        }
        if (product.getUpdatedAt() == null) {
            product.setUpdatedAt(Instant.now());
        }
        // Validaciones de negocio, etc.
        return productRepository.save(product);
    }

    public Optional<Product> getProductById(UUID id) {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProduct(UUID id, Product updatedProduct) {
        return productRepository.findById(id).map(existingProduct -> {
            // Actualizar campos relevantes
            existingProduct.setSku(updatedProduct.getSku());
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setPurchasePrice(updatedProduct.getPurchasePrice());
            existingProduct.setSalePrice(updatedProduct.getSalePrice());
            existingProduct.setCurrentStock(updatedProduct.getCurrentStock());
            existingProduct.setTaxId(updatedProduct.getTaxId());
            existingProduct.setSupplierId(updatedProduct.getSupplierId());
            existingProduct.setUpdatedAt(Instant.now());
            // No actualizamos created_at, created_by_user_id, etc.
            return productRepository.save(existingProduct);
        }).orElseThrow(() -> new RuntimeException("Product not found with ID: " + id)); // Manejo de error básico
    }

    public void deleteProduct(UUID id) {
        // En un sistema real, probablemente haríamos un "soft delete" (marcar como deleted_at)
        productRepository.deleteById(id);
    }

    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }
}
