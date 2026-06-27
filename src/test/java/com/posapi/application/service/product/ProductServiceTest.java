package com.posapi.application.service.product;

import com.posapi.domain.model.product.Product;
import com.posapi.domain.repository.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


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
                .currentStock(100)
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
                .currentStock(100)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void createProduct_shouldReturnCreatedProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        Product createdProduct = productService.createProduct(product1);

        assertNotNull(createdProduct.getId());
        assertEquals("SKU001", createdProduct.getSku());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getProductById_shouldReturnProduct_whenFound() {
        when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));

        Optional<Product> foundProduct = productService.getProductById(product1.getId());

        assertTrue(foundProduct.isPresent());
        assertEquals(product1.getName(), foundProduct.get().getName());
        verify(productRepository, times(1)).findById(product1.getId());
    }

    @Test
    void getProductById_shouldReturnEmpty_whenNotFound() {
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productService.getProductById(UUID.randomUUID());

        assertFalse(foundProduct.isPresent());
        verify(productRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void getAllProducts_shouldReturnListOfProducts() {
        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(products);

        List<Product> allProducts = productService.getAllProducts();

        assertNotNull(allProducts);
        assertEquals(2, allProducts.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct_whenFound() {
        UUID id = UUID.randomUUID();
        Product updatedDetails = new Product(); // Crear un objeto Product vacío
        updatedDetails.setId(id);
        updatedDetails.setSku("SKU001-UPDATED");
        updatedDetails.setName("Product 1 Updated");
        updatedDetails.setDescription("Description 1 Updated");
        updatedDetails.setPurchasePrice(BigDecimal.valueOf(12.0));
        updatedDetails.setSalePrice(BigDecimal.valueOf(17.0));
        updatedDetails.setCurrentStock(105);

        when(productRepository.findById(id)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedDetails); // Mock the save operation

        Product result = productService.updateProduct(id, updatedDetails);
        assertNotNull(result);
        assertEquals("SKU001-UPDATED", result.getSku());
        assertEquals("Product 1 Updated", result.getName());
        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        Product updatedDetails = new Product(); // Crear un objeto Product vacío
        updatedDetails.setId(id);
        updatedDetails.setName("Non Existent");

        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productService.updateProduct(id, updatedDetails)
        );

        assertTrue(exception.getMessage().startsWith("Product not found with ID: "));
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

        assertTrue(foundProduct.isPresent());
        assertEquals(product1.getName(), foundProduct.get().getName());
        verify(productRepository, times(1)).findBySku(product1.getSku());
    }

    @Test
    void getProductBySku_shouldReturnEmpty_whenNotFound() {
        when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productService.getProductBySku("NON_EXISTENT_SKU");

        assertFalse(foundProduct.isPresent());
        verify(productRepository, times(1)).findBySku(anyString());
    }
}
