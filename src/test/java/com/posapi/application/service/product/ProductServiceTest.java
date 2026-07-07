package com.posapi.application.service.product;

import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.product.Product;
import com.posapi.domain.port.output.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
                .salePrice(BigDecimal.valueOf(100))
                .build();

        product2 = Product.builder()
                .id(UUID.randomUUID())
                .sku("SKU002")
                .name("Product 2")
                .salePrice(BigDecimal.valueOf(200))
                .build();
    }

    @Test
    void createProductShouldSaveAndReturnProductWhenSkuIsUnique() {
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        Product newProduct = Product.builder().sku("SKU001").name("New Product").build();
        Product createdProduct = productService.createProduct(newProduct);

        assertNotNull(createdProduct);
        assertNotNull(createdProduct.getId());
        assertEquals("SKU001", createdProduct.getSku());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProductShouldThrowDuplicateResourceExceptionWhenSkuExists() {
        when(productRepository.existsBySku("SKU001")).thenReturn(true);

        Product newProduct = Product.builder().sku("SKU001").name("New Product").build();

        assertThrows(DuplicateResourceException.class, () -> {
            productService.createProduct(newProduct);
        });

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getAllProductsShouldReturnListOfProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductByIdShouldReturnProductWhenFound() {
        UUID id = product1.getId();
        when(productRepository.findById(id)).thenReturn(Optional.of(product1));

        Optional<Product> foundProduct = productService.getProductById(id);

        assertTrue(foundProduct.isPresent());
        assertEquals(product1, foundProduct.get());
    }

    @Test
    void updateProductShouldUpdateAndReturnProductWhenFound() {
        UUID id = product1.getId();
        Product updatedDetails = Product.builder().name("Updated Name").build();

        when(productRepository.findById(id)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Product> result = productService.updateProduct(id, updatedDetails);

        assertTrue(result.isPresent());
        assertEquals("Updated Name", result.get().getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProductShouldReturnEmptyWhenNotFound() {
        UUID id = UUID.randomUUID();
        Product updatedDetails = Product.builder().name("Updated Name").build();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Product> result = productService.updateProduct(id, updatedDetails);

        assertFalse(result.isPresent());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProductShouldCallRepositoryDelete() {
        UUID id = product1.getId();
        doNothing().when(productRepository).deleteById(id);

        productService.deleteProduct(id);

        verify(productRepository, times(1)).deleteById(id);
    }
    
    @Test
    void getProductBySkuShouldReturnProductWhenSkuExists() {
        when(productRepository.findBySku(product1.getSku())).thenReturn(Optional.of(product1));

        Optional<Product> foundProduct = productService.getProductBySku(product1.getSku());

        assertTrue(foundProduct.isPresent());
        assertEquals(product1.getSku(), foundProduct.get().getSku());
        verify(productRepository, times(1)).findBySku(product1.getSku());
    }

    @Test
    void getProductBySkuShouldReturnEmptyWhenSkuDoesNotExist() {
        when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productService.getProductBySku("NON_EXISTENT_SKU");

        assertFalse(foundProduct.isPresent());
        verify(productRepository, times(1)).findBySku(anyString());
    }
}
