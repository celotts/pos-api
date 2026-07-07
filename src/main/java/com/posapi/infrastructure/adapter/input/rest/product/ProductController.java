package com.posapi.infrastructure.adapter.input.rest.product;

import com.posapi.application.port.product.ProductManagementPort;
import com.posapi.domain.model.product.Product;
import com.posapi.domain.model.user.User;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductRequest;
import com.posapi.infrastructure.security.SecurityContextHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ProductController {

    private final ProductManagementPort productManagementPort;
    private final SecurityContextHelper securityContextHelper;

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        Product product = Product.builder()
                .sku(request.sku()).name(request.name()).description(request.description())
                .purchasePrice(request.purchasePrice()).salePrice(request.salePrice())
                .currentStock(request.currentStock()).categoryId(request.categoryId())
                .taxId(request.taxId()).supplierId(request.supplierId())
                .createdBy(currentUser.getId())
                .build();
        Product createdProduct = productManagementPort.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productManagementPort.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID id) {
        return productManagementPort.getProductById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductRequest request) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        Product product = Product.builder()
                .sku(request.sku()).name(request.name()).description(request.description())
                .purchasePrice(request.purchasePrice()).salePrice(request.salePrice())
                .currentStock(request.currentStock()).categoryId(request.categoryId())
                .taxId(request.taxId()).supplierId(request.supplierId())
                .updatedBy(currentUser.getId())
                .build();
        return productManagementPort.updateProduct(id, product)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productManagementPort.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
