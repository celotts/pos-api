package com.posapi.application.service.product;

import com.posapi.application.port.product.ProductManagementPort; // Importar la interfaz
import com.posapi.domain.model.product.Product;
import com.posapi.domain.repository.product.ProductRepository;
import jakarta.validation.constraints.NotNull; // Importar NotNull
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService implements ProductManagementPort { // Implementa la interfaz

    private final ProductRepository productRepository;
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @NotNull
    public Product createProduct(@NotNull Product product) {
        // Aquí se podría añadir lógica de negocio antes de guardar
        // Validaciones de negocio, etc.
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> getProductById(UUID id) {
        return productRepository.findById(id);
    }

    @Override
    @NotNull
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @NotNull
    public Product updateProduct(UUID id, @NotNull Product updatedProduct) {
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

    @Override
    public void deleteProduct(UUID id) {
        // En un sistema real, probablemente haríamos un "soft delete" (marcar como deleted_at)
        productRepository.deleteById(id);
    }

    @Override
    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }
}

