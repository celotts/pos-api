package com.posapi.application.service.product;

import com.posapi.domain.exception.ResourceNotFoundException;
import com.posapi.domain.model.product.Product;
import com.posapi.domain.port.output.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = Product.builder()
                .id(UUID.randomUUID())
                .sku("SKU001")
                .name("Product 1")
                .description("Description 1")
                .purchasePrice(BigDecimal.valueOf(10.0))
                .salePrice(BigDecimal.valueOf(15.0))
                .currentStock(new BigDecimal("100.00")) // 🛡️ FIX: Use BigDecimal for stock
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        product2 = Product.builder()
                .id(UUID.randomUUID())
                .sku("SKU001")
                .name("Product 1")
                .description("Description 1")
                .purchasePrice(BigDecimal.valueOf(10.0))
                .salePrice(BigDecimal.valueOf(15.0))
                .currentStock(new BigDecimal("100.00")) // 🛡️ FIX: Use BigDecimal for stock
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void createProduct_shouldReturnCreatedProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        Product createdProduct = productService.createProduct(product1);

        assertThat(createdProduct.getId()).isNotNull();
        assertThat(createdProduct.getSku()).isEqualTo("SKU001");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getProductById_shouldReturnProduct_whenFound() {
        when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));

        Optional<Product> foundProduct = productService.getProductById(product1.getId());

        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getId()).isEqualTo(product1.getId());
        assertThat(foundProduct.get().getName()).isEqualTo(product1.getName());

        verify(productRepository, times(1)).findById(product1.getId());
    }

    @Test
    void getProductById_shouldReturnEmpty_whenNotFound() {
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productService.getProductById(UUID.randomUUID());

        assertThat(foundProduct).isNotPresent();
        verify(productRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void getAllProducts_shouldReturnListOfProducts() {
        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(products);

        List<Product> allProducts = productService.getAllProducts();

        assertThat(allProducts).isNotNull();
        assertThat(allProducts).hasSize(2);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct_whenFound() {
        UUID id = UUID.randomUUID();
        // This is the original product in the database
        Product existingProduct = Product.builder()
                .id(id)
                .name("Old Name")
                .currentStock(new BigDecimal("50.0"))
                .build();

        // This is the update request data
        Product updatedDetails = Product.builder()
                .name("New Name")
                .currentStock(new BigDecimal("75.5"))
                .build();

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        // The save method should return the product that was passed to it.
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.updateProduct(id, updatedDetails);

        // 🛡️ World-Class: Use ArgumentCaptor to verify the state of the object passed to save()
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product savedProduct = productCaptor.getValue();

        assertThat(savedProduct.getName()).isEqualTo("New Name");
        assertThat(savedProduct.getCurrentStock()).isEqualTo(new BigDecimal("75.5"));
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void updateProduct_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        Product updatedDetails = Product.builder().name("Non Existent").build();

        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // 🛡️ World-Class: Assert for a specific, custom exception, not a generic one.
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                productService.updateProduct(id, updatedDetails)
        );

        assertThat(exception.getMessage()).isEqualTo("Product not found with ID: " + id);
        verify(productRepository, times(1)).findById(any(UUID.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_shouldCallRepositoryDelete() {
        doNothing().when(productRepository).deleteById(product1.getId());

        productService.deleteProduct(product1.getId());

        verify(productRepository, times(1)).deleteById(product1.getId());
    }

    @Test
    void getProductBySku_shouldReturnProduct_whenFound() {
        when(productRepository.findBySku(product1.getSku())).thenReturn(Optional.of(product1));

        Optional<Product> foundProduct = productService.getProductBySku(product1.getSku());

        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo(product1.getName());
        verify(productRepository, times(1)).findBySku(product1.getSku());
    }

    @Test
    void getProductBySku_shouldReturnEmpty_whenNotFound() {
        when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productService.getProductBySku("NON_EXISTENT_SKU");

        assertThat(foundProduct).isNotPresent();
        verify(productRepository, times(1)).findBySku(anyString());
    }
}
